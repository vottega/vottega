package vottega.room_service.service.impl

import jakarta.transaction.Transactional
import main.room_service.avro.RoomAvro
import org.apache.kafka.clients.consumer.Consumer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.kafka.test.context.EmbeddedKafka
import vottega.room_service.domain.enumeration.RoomStatus
import vottega.room_service.dto.ParticipantRoleDTO
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
class RoomServiceImplTest {

  @Autowired
  private lateinit var roomRepository: RoomRepository

  @Autowired
  private lateinit var roomService: RoomService

  @Autowired
  private lateinit var roomKafkaConsumer: Consumer<Long, RoomAvro>


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

  private fun getNextRoomAvro(): RoomAvro {
    val records = roomKafkaConsumer.poll(Duration.ofSeconds(10))
    assertThat(records.count()).isEqualTo(1)
    return records.iterator().next().value()
  }

}