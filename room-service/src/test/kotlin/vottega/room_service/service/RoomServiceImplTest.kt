package vottega.room_service.service

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import vottega.room_service.adaptor.RoomProducer
import vottega.room_service.domain.enumeration.RoomStatus
import vottega.room_service.dto.ParticipantInfoDTO
import vottega.room_service.dto.ParticipantRoleDTO
import vottega.room_service.exception.RoomStatusConflictException
import vottega.room_service.repository.RoomRepository

@Transactional
@SpringBootTest( //TODO SpringBootTest 없애기 단위 테스트로 변환
  properties = [
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
  ]
)
@ActiveProfiles("test")
class RoomServiceImplTest {
  @Autowired
  private lateinit var roomService: RoomService

  @MockBean
  private lateinit var roomProducer: RoomProducer

  @Autowired
  private lateinit var roomRepository: RoomRepository

  @Autowired
  private lateinit var entityManager: EntityManager


  @Test
  fun `방 생성 시에 DTO와 같은 값으로 생성이 되어야 한다`() {
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
  fun `방 이름 변경 시에 방 이름이 정확히 변경이 되어야 한다`() {
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
  }

  @Test
  fun `방 상태가 NOT STARTED에서 PROGRESS로 바꿀 수 있어야 한다`() {
    val roomName = "testRoom"
    val ownerId = 1L
    val roomResponseDTO = roomService.createRoom(roomName, ownerId, listOf())

    val newStatus = RoomStatus.PROGRESS
    val successRoomName = "success room name"
    val updatedRoom = roomService.updateRoom(roomResponseDTO.id, successRoomName, newStatus)

    val foundRoom = roomRepository.findById(roomResponseDTO.id)
    assertThat(updatedRoom.name).isEqualTo(foundRoom.get().roomName)
    assertThat(updatedRoom.status).isEqualTo(newStatus)
  }

  @Test
  fun `방 상태는 NOT STARTED에서는 PROGRESS로만 바꿀 수 있다`() {
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
  }

  @Test
  fun `방 상태는 NOT PROGRESS에서 STOPPED로 바꿀 수 있어야 한다`() {
    val roomName = "testRoom"
    val ownerId = 1L
    val roomResponseDTO = roomService.createRoom(roomName, ownerId, listOf())
    roomService.updateRoom(roomResponseDTO.id, null, RoomStatus.PROGRESS)

    val newStatus = RoomStatus.STOPPED
    val successRoomName = "success room name"
    val updatedRoom = roomService.updateRoom(roomResponseDTO.id, successRoomName, newStatus)

    val foundRoom = roomRepository.findById(roomResponseDTO.id)
    assertThat(updatedRoom.name).isEqualTo(foundRoom.get().roomName)
    assertThat(updatedRoom.status).isEqualTo(newStatus)
  }

  @Test
  fun `방 상태가 NOT PROGRESS에서 FINISHED로 바꿀 수 있어야 한다`() {
    val roomName = "testRoom"
    val ownerId = 1L
    val roomResponseDTO = roomService.createRoom(roomName, ownerId, listOf())
    roomService.updateRoom(roomResponseDTO.id, null, RoomStatus.PROGRESS)

    val newStatus = RoomStatus.FINISHED
    val successRoomName = "success room name"
    val updatedRoom = roomService.updateRoom(roomResponseDTO.id, successRoomName, newStatus)

    val foundRoom = roomRepository.findById(roomResponseDTO.id)
    assertThat(updatedRoom.name).isEqualTo(foundRoom.get().roomName)
    assertThat(updatedRoom.status).isEqualTo(newStatus)
  }


  @Test
  fun `방 상태는 STOPPED에서 PROGRESS로 바꿀 수 있어야 한다`() {

    val roomName = "testRoom"
    val ownerId = 1L
    val roomResponseDTO = roomService.createRoom(roomName, ownerId, listOf())

    roomService.updateRoom(roomResponseDTO.id, null, RoomStatus.PROGRESS)
    roomService.updateRoom(roomResponseDTO.id, null, RoomStatus.STOPPED)

    val newStatus = RoomStatus.PROGRESS
    val successRoomName = "success room name"
    val updatedRoom = roomService.updateRoom(roomResponseDTO.id, successRoomName, newStatus)

    //then
    val foundRoom = roomRepository.findById(roomResponseDTO.id)
    assertThat(updatedRoom.name).isEqualTo(foundRoom.get().roomName)
    assertThat(updatedRoom.status).isEqualTo(newStatus)
  }

  @Test
  fun `침가자 추가 시에 참가자 정보가 정확히 저장이 되어야 한다`() {
    val roomName = "testRoom"
    val ownerId = 1L
    val participantRoleDTO1 = ParticipantRoleDTO("voter", true)
    val participantRoleDTO2 = ParticipantRoleDTO("viewer", false)
    val roomResponseDTO = roomService.createRoom(roomName, ownerId, listOf(participantRoleDTO1, participantRoleDTO2))

    val participant1 = ParticipantInfoDTO("민균", null, "회장", "voter")
    val participant2 = ParticipantInfoDTO("성윤", null, "따까리", "viewer")
    roomService.addParticipant(roomResponseDTO.id, listOf(participant1, participant2))

    val foundRoom = roomService.getRoom(roomResponseDTO.id)
    assertThat(foundRoom.participants.size).isEqualTo(2)
    assertThat(foundRoom.participants).anyMatch({ it.name == participant1.name && it.participantRole.role == participant1.role && it.participantRole.canVote })
    assertThat(foundRoom.participants).anyMatch({ it.name == participant2.name && it.participantRole.role == participant2.role && !it.participantRole.canVote })
  }

  @Test
  @DisplayName("참가자 삭제 테스트")
  fun `참가자 삭제 시에 해당 참가자만 방에서 제거되어야 한다`() {
    val roomName = "testRoom"
    val ownerId = 1L
    val participantRoleDTO1 = ParticipantRoleDTO("voter", true)
    val participantRoleDTO2 = ParticipantRoleDTO("viewer", false)
    val roomResponseDTO = roomService.createRoom(roomName, ownerId, listOf(participantRoleDTO1, participantRoleDTO2))
    val participant1 = ParticipantInfoDTO("민균", null, "회장", "voter")
    val participant2 = ParticipantInfoDTO("성윤", null, "따까리", "viewer")
    roomService.addParticipant(roomResponseDTO.id, listOf(participant1, participant2))

    val foundRoom = roomService.getRoom(roomResponseDTO.id)
    val participantId1 = foundRoom.participants[0].id
    val participantId2 = foundRoom.participants[1].id
    roomService.removeParticipant(roomResponseDTO.id, participantId1)
    entityManager.flush()

    val foundRoom2 = roomService.getRoom(roomResponseDTO.id)
    assertThat(foundRoom2.participants.size).isEqualTo(1)
    assertThat(foundRoom2.participants).noneMatch({ it.id == participantId1 })
    assertThat(foundRoom2.participants).anyMatch({ it.id == participantId2 })
  }


  @Test
  fun `역할 추가 시에 DTO와 같은 정보로 추가가 되고 삭제 시에 해당 역할만 제거되어야 한다`() {
    val roomName = "testRoom"
    val ownerId = 1L
    val participantRoleDTO1 = ParticipantRoleDTO("voter", true)
    val participantRoleDTO2 = ParticipantRoleDTO("viewer", false)
    val roomResponseDTO = roomService.createRoom(roomName, ownerId, listOf(participantRoleDTO1, participantRoleDTO2))

    val newRole = ParticipantRoleDTO("newRole", true)
    roomService.addRole(roomResponseDTO.id, newRole)
    
    val foundRoom = roomService.getRoom(roomResponseDTO.id)
    assertThat(foundRoom.roles.size).isEqualTo(3)
    assertThat(foundRoom.roles).anyMatch({ it.role == newRole.role && it.canVote })

    roomService.deleteRole(roomResponseDTO.id, newRole.role)
    val foundRoom2 = roomService.getRoom(roomResponseDTO.id)
    assertThat(foundRoom2.roles.size).isEqualTo(2)
    assertThat(foundRoom2.roles).noneMatch({ it.role == newRole.role })
  }
}