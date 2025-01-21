package vottega.room_service.service.impl

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import main.room_service.avro.RoomAvro
import org.apache.kafka.clients.consumer.Consumer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.kafka.test.context.EmbeddedKafka
import vottega.room_service.avro.ParticipantAvro
import vottega.room_service.domain.enumeration.RoomStatus
import vottega.room_service.dto.ParticipantInfoDTO
import vottega.room_service.dto.ParticipantRoleDTO
import vottega.room_service.dto.mapper.ParticipantRoleMapper
import vottega.room_service.exception.RoomStatusConflictException
import vottega.room_service.repository.RoomRepository
import vottega.room_service.service.RoomService
import java.time.Duration

@Transactional
@EmbeddedKafka(
  partitions = 1,
  topics = ["room", "participant"]
)
@SpringBootTest
@Import(TestConfig::class)
@Execution(ExecutionMode.SAME_THREAD)
class RoomServiceImplTest {

  @Autowired
  private lateinit var participantRoleMapper: ParticipantRoleMapper

  @Autowired
  private lateinit var roomRepository: RoomRepository

  @Autowired
  private lateinit var roomService: RoomService

  @Autowired
  private lateinit var roomKafkaConsumer: Consumer<Long, RoomAvro>

  @Autowired
  private lateinit var participantKafkaConsumer: Consumer<Long, ParticipantAvro>

  @Autowired
  private lateinit var entityManager: EntityManager


  @Test
  @DisplayName("방을 생성 후 확인 테스트")
  fun createRoomTest() {
    //given
    val roomName = "testRoom"
    val ownerId = 1L
    val participantRoleList = listOf(
      ParticipantRoleDTO("owner", true),
      ParticipantRoleDTO("voter", true)
    )
    //when
    val roomResponseDTO = roomService.createRoom(roomName, ownerId, participantRoleList)

    //then
    val room = roomRepository.findById(roomResponseDTO.id).get()
    assertThat(room.roomName).isEqualTo(roomName)
    assertThat(room.ownerId).isEqualTo(ownerId)
    assertThat(room.participantRoleList.size).isEqualTo(participantRoleList.size)
    assertThat(room.participantRoleList).anyMatch({ it.role == "owner" && it.canVote })
    assertThat(room.participantRoleList).anyMatch({ it.role == "voter" && it.canVote })
  }

  @Test
  @DisplayName("방 이름 변경 테스트 | 카프카 메시지 전송 확인")
  fun updateRoomName() {
    //given
    val roomName = "testRoom"
    val ownerId = 1L
    val roomResponseDTO = roomService.createRoom(roomName, ownerId, listOf())

    //when
    val newRoomName = "newRoomName"
    val updatedRoom = roomService.updateRoom(roomResponseDTO.id, newRoomName, null)

    //then
    val foundRoom = roomRepository.findById(roomResponseDTO.id)
    assertThat(updatedRoom.name).isEqualTo(foundRoom.get().roomName)
    val records = roomKafkaConsumer.poll(Duration.ofSeconds(10))
    assertThat(records.count()).isEqualTo(1)
    val record = records.iterator().next()
    assertThat(record.value().id).isEqualTo(roomResponseDTO.id)
    assertThat(record.value().roomName).isEqualTo(newRoomName)
  }

  @Test
  @DisplayName("방 상태 변경 테스트 | 상태 변경 NOT STARTED -> PROGRESS")
  fun updateStatus1() {
    //given
    val roomName = "testRoom"
    val ownerId = 1L
    val roomResponseDTO = roomService.createRoom(roomName, ownerId, listOf())

    //when
    val newStatus = RoomStatus.PROGRESS
    val successRoomName = "success room name"
    val updatedRoom = roomService.updateRoom(roomResponseDTO.id, successRoomName, newStatus)

    //then
    val foundRoom = roomRepository.findById(roomResponseDTO.id)
    assertThat(updatedRoom.name).isEqualTo(foundRoom.get().roomName)
    assertThat(updatedRoom.status).isEqualTo(newStatus)
    val roomAvro = getNextRoomAvro()
    assertThat(roomAvro.id).isEqualTo(roomResponseDTO.id)
    assertThat(roomAvro.roomName).isEqualTo(successRoomName)
  }

  @Test
  @DisplayName("방 상태 변경 테스트 | 상태 변경 NOT STARTED -> FINISHED | 실패해서 카프카에 전송이 안와야 함")
  fun updateStatus2() {
    //given
    val roomName = "testRoom"
    val ownerId = 1L
    val roomResponseDTO = roomService.createRoom(roomName, ownerId, listOf())

    //when
    assertThrows<RoomStatusConflictException> {
      roomService.updateRoom(roomResponseDTO.id, null, RoomStatus.FINISHED)
    }
    assertThrows<RoomStatusConflictException> {
      roomService.updateRoom(roomResponseDTO.id, null, RoomStatus.STOPPED)
    }
    assertThrows<RoomStatusConflictException> {
      roomService.updateRoom(roomResponseDTO.id, null, RoomStatus.NOT_STARTED)
    }

    //then
    val foundRoom = roomRepository.findById(roomResponseDTO.id)
    assertThat(foundRoom.get().status).isEqualTo(RoomStatus.NOT_STARTED)
    val records = roomKafkaConsumer.poll(Duration.ofSeconds(10))
    assertThat(records.count()).isEqualTo(0)
  }

  @Test
  @DisplayName("방 상태 변경 테스트 | 상태 변경 PROGRESS -> STOPPED")
  fun updateStatus3() {
    //given
    val roomName = "testRoom"
    val ownerId = 1L
    val roomResponseDTO = roomService.createRoom(roomName, ownerId, listOf())
    roomService.updateRoom(roomResponseDTO.id, null, RoomStatus.PROGRESS)
    getNextRoomAvro()

    //when
    val newStatus = RoomStatus.STOPPED
    val successRoomName = "success room name"
    val updatedRoom = roomService.updateRoom(roomResponseDTO.id, successRoomName, newStatus)

    //then
    val foundRoom = roomRepository.findById(roomResponseDTO.id)
    assertThat(updatedRoom.name).isEqualTo(foundRoom.get().roomName)
    assertThat(updatedRoom.status).isEqualTo(newStatus)
    val roomAvro = getNextRoomAvro()
    assertThat(roomAvro.id).isEqualTo(roomResponseDTO.id)
    assertThat(roomAvro.roomName).isEqualTo(successRoomName)
  }

  @Test
  @DisplayName("방 상태 변경 테스트 | 상태 변경 PROGRESS -> ?")
  fun updateStatus4() {
    //given
    val roomName = "testRoom"
    val ownerId = 1L
    val roomResponseDTO = roomService.createRoom(roomName, ownerId, listOf())
    roomService.updateRoom(roomResponseDTO.id, null, RoomStatus.PROGRESS)
    getNextRoomAvro()

    //when
    val newStatus = RoomStatus.FINISHED
    val successRoomName = "success room name"
    val updatedRoom = roomService.updateRoom(roomResponseDTO.id, successRoomName, newStatus)

    //then
    val foundRoom = roomRepository.findById(roomResponseDTO.id)
    assertThat(updatedRoom.name).isEqualTo(foundRoom.get().roomName)
    assertThat(updatedRoom.status).isEqualTo(newStatus)
    val roomAvro = getNextRoomAvro()
    assertThat(roomAvro.id).isEqualTo(roomResponseDTO.id)
    assertThat(roomAvro.roomName).isEqualTo(successRoomName)
  }

  @Test
  @DisplayName("방 상태 변경 테스트 | 상태 변경 NOT STARTED -> FINISHED | 실패해서 카프카에 전송이 안와야 함")
  fun updateStatus5() {
    //given
    val roomName = "testRoom"
    val ownerId = 1L
    val roomResponseDTO = roomService.createRoom(roomName, ownerId, listOf())
    roomService.updateRoom(roomResponseDTO.id, null, RoomStatus.PROGRESS)
    getNextRoomAvro()

    //when
    assertThrows<RoomStatusConflictException> {
      roomService.updateRoom(roomResponseDTO.id, null, RoomStatus.PROGRESS)
    }
    assertThrows<RoomStatusConflictException> {
      roomService.updateRoom(roomResponseDTO.id, null, RoomStatus.NOT_STARTED)
    }

    //then
    val foundRoom = roomRepository.findById(roomResponseDTO.id)
    assertThat(foundRoom.get().status).isEqualTo(RoomStatus.PROGRESS)
    val records = roomKafkaConsumer.poll(Duration.ofSeconds(10))
    assertThat(records.count()).isEqualTo(0)
  }

  @Test
  @DisplayName("방 상태 변경 테스트 | 상태 변경 STOPPED -> PROGRESS")
  fun updateStatus6() {
    //given
    val roomName = "testRoom"
    val ownerId = 1L
    val roomResponseDTO = roomService.createRoom(roomName, ownerId, listOf())
    roomService.updateRoom(roomResponseDTO.id, null, RoomStatus.PROGRESS)
    getNextRoomAvro()
    roomService.updateRoom(roomResponseDTO.id, null, RoomStatus.STOPPED)
    getNextRoomAvro()
    //when
    val newStatus = RoomStatus.PROGRESS
    val successRoomName = "success room name"
    val updatedRoom = roomService.updateRoom(roomResponseDTO.id, successRoomName, newStatus)

    //then
    val foundRoom = roomRepository.findById(roomResponseDTO.id)
    assertThat(updatedRoom.name).isEqualTo(foundRoom.get().roomName)
    assertThat(updatedRoom.status).isEqualTo(newStatus)
    val roomAvro = getNextRoomAvro()
    assertThat(roomAvro.id).isEqualTo(roomResponseDTO.id)
    assertThat(roomAvro.roomName).isEqualTo(successRoomName)
  }

  @Test
  @DisplayName("참가자 추가 테스트")
  fun addParticipant() {
    //given
    val roomName = "testRoom"
    val ownerId = 1L
    val participantRoleDTO1 = ParticipantRoleDTO("voter", true)
    val participantRoleDTO2 = ParticipantRoleDTO("viewer", false)
    val roomResponseDTO = roomService.createRoom(roomName, ownerId, listOf(participantRoleDTO1, participantRoleDTO2))

    //when
    val participant1 = ParticipantInfoDTO("민균", null, "회장", "voter")
    val participant2 = ParticipantInfoDTO("성윤", null, "따까리", "viewer")
    roomService.addParticipant(roomResponseDTO.id, listOf(participant1, participant2))

    //then
    val foundRoom = roomService.getRoom(roomResponseDTO.id)
    assertThat(foundRoom.participants.size).isEqualTo(2)
    assertThat(foundRoom.participants).anyMatch({ it.name == participant1.name && it.participantRole.role == participant1.role && it.participantRole.canVote })
    assertThat(foundRoom.participants).anyMatch({ it.name == participant2.name && it.participantRole.role == participant2.role && !it.participantRole.canVote })

    val records = participantKafkaConsumer.poll(Duration.ofSeconds(3))
    println(records)
    println(records.count())
    assertThat(records.count()).isEqualTo(2)
    val participant1Avro = records.find { it.value().name == participant1.name }!!.value()
    assertThat(participant1Avro.name).isEqualTo(participant1.name)
    val participant2Avro = records.find { it.value().name == participant2.name }!!.value()
    assertThat(participant2Avro.name).isEqualTo(participant2.name)
  }

  @Test
  @DisplayName("참가자 삭제 테스트")
  fun removeParticipant() {
    //given
    val roomName = "testRoom"
    val ownerId = 1L
    val participantRoleDTO1 = ParticipantRoleDTO("voter", true)
    val participantRoleDTO2 = ParticipantRoleDTO("viewer", false)
    val roomResponseDTO = roomService.createRoom(roomName, ownerId, listOf(participantRoleDTO1, participantRoleDTO2))
    val participant1 = ParticipantInfoDTO("민균", null, "회장", "voter")
    val participant2 = ParticipantInfoDTO("성윤", null, "따까리", "viewer")
    roomService.addParticipant(roomResponseDTO.id, listOf(participant1, participant2))

    //when
    val foundRoom = roomService.getRoom(roomResponseDTO.id)
    val participantId1 = foundRoom.participants[0].id
    val participantId2 = foundRoom.participants[1].id
    roomService.removeParticipant(roomResponseDTO.id, participantId1)
    entityManager.flush()

    //then// null은 잘되는데 필터가 안된다 ㅅㅂ
    val foundRoom2 = roomService.getRoom(roomResponseDTO.id)
    assertThat(foundRoom2.participants.size).isEqualTo(1)
    assertThat(foundRoom2.participants).noneMatch({ it.id == participantId1 })
    assertThat(foundRoom2.participants).anyMatch({ it.id == participantId2 })
  }

  @AfterEach
  fun tearDown() {
    // 의심 1 : consumer이 factory가 아니여서 그런듯?
//    roomKafkaConsumer.poll(Duration.ofSeconds(3))
//    participantKafkaConsumer.poll(Duration.ofSeconds(3))
  }

  private fun getNextRoomAvro(): RoomAvro {
    val records = roomKafkaConsumer.poll(Duration.ofSeconds(3))
    return records.iterator().next().value()
  }

}