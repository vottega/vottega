package vottega.room_service.service.impl

import jakarta.transaction.Transactional
import main.room_service.avro.RoomAvro
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import vottega.room_service.config.KafkaCommonConfig
import vottega.room_service.dto.ParticipantRoleDTO
import vottega.room_service.repository.RoomRepository
import vottega.room_service.service.RoomService
import java.time.Duration

//TODO EMBEDDED KAFKA를 사용하여 테스트 코드 작성
@Transactional
@SpringBootTest
class RoomServiceImplTest {
  @Autowired
  private lateinit var roomRepository: RoomRepository

  @Autowired
  private lateinit var roomService: RoomService

  @Autowired
  private lateinit var kafkaCommonConfig: KafkaCommonConfig


  private lateinit var kafkaConsumer: KafkaConsumer<String, RoomAvro>


  @BeforeEach
  fun setUp() {
    val consumerProps = kafkaCommonConfig.commonConsumerConfig()
    kafkaConsumer = KafkaConsumer<String, RoomAvro>(consumerProps)
    kafkaConsumer.subscribe(listOf("room"))
  }

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
    val records = kafkaConsumer.poll(Duration.ofSeconds(10))
    println("records : ${records.count()}")
    println(records.first())

  }
}