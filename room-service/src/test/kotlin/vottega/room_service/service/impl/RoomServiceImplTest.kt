package vottega.room_service.service.impl

import jakarta.transaction.Transactional
import main.room_service.avro.RoomAvro
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.test.context.EmbeddedKafka
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import vottega.room_service.config.KafkaCommonConfig
import vottega.room_service.dto.ParticipantRoleDTO
import vottega.room_service.repository.RoomRepository
import vottega.room_service.service.RoomService
import java.time.Duration

@Transactional
@SpringBootTest
@EmbeddedKafka(
  partitions = 1, topics = ["room"]
)
class RoomServiceImplTest {
  @Autowired
  private lateinit var roomRepository: RoomRepository

  @Autowired
  private lateinit var roomService: RoomService

  @Autowired
  private lateinit var kafkaCommonConfig: KafkaCommonConfig

  private val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))

  private lateinit var kafkaConsumer: KafkaConsumer<String, String>


  @BeforeEach
  fun setUp() {
    kafkaContainer.start()
    val factory = ConcurrentKafkaListenerContainerFactory<Long, RoomAvro>()
    val consumerProps = kafkaCommonConfig.commonConsumerConfig()
    val consumer = KafkaConsumer<String, RoomAvro>(consumerProps)
    consumer.subscribe(listOf("room"))
  }

  @AfterEach
  fun tearDown() {
    kafkaContainer.stop()
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
    val records = kafkaConsumer.poll(Duration.ofSeconds(1))
    val record = records.first()
    println(record.value())

  }

  @Test
  fun addParticipant() {
  }

  @Test
  fun removeParticipant() {
  }

  @Test
  fun updateParticipant() {
  }

  @Test
  fun enterParticipant() {
  }

  @Test
  fun exitParticipant() {
  }

  @Test
  fun addRole() {
  }

  @Test
  fun deleteRole() {
  }

  @Test
  fun getRoom() {
  }

  @Test
  fun getRoomList() {
  }
}