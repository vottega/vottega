package vottega.room_service.service.impl

import jakarta.transaction.Transactional
import main.room_service.avro.RoomAvro
import org.apache.kafka.clients.consumer.Consumer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import vottega.room_service.adaptor.impl.RoomProducerImpl
import vottega.room_service.avro.ParticipantAvro
import vottega.room_service.dto.ParticipantRoleDTO
import vottega.room_service.dto.mapper.ParticipantMapper
import vottega.room_service.dto.mapper.RoomMapper
import vottega.room_service.repository.RoomRepository
import java.time.Duration

@Transactional
@EmbeddedKafka(
  partitions = 1,
  topics = ["room", "participant"]
)
@SpringBootTest
class RoomServiceImplTest {
  @Autowired
  private lateinit var embeddedKafka: EmbeddedKafkaBroker

  @Autowired
  private lateinit var roomMapper: RoomMapper

  @Autowired
  private lateinit var participantMapper: ParticipantMapper

  @Autowired
  private lateinit var roomRepository: RoomRepository

  private lateinit var roomService: RoomServiceImpl


  private lateinit var roomKafkaTemplate: KafkaTemplate<Long, RoomAvro>
  private lateinit var participantKafkaTemplate: KafkaTemplate<Long, ParticipantAvro>


  private lateinit var roomProducer: RoomProducerImpl
  private lateinit var roomKafkaConsumer: Consumer<Long, RoomAvro>
  private lateinit var participantKafkaConsumer: Consumer<Long, ParticipantAvro>


  @BeforeEach
  fun setUp() {
    // ProducerFactory 생성
    val producerProps = mutableMapOf<String, Any>(
      "bootstrap.servers" to embeddedKafka.brokersAsString,
      "key.serializer" to org.apache.kafka.common.serialization.LongSerializer::class.java,
      "value.serializer" to io.confluent.kafka.serializers.KafkaAvroSerializer::class.java,
      "schema.registry.url" to "mock://embedded-schema-registry"
    )
    val producerFactory = DefaultKafkaProducerFactory<Long, RoomAvro>(producerProps)
    roomKafkaTemplate = KafkaTemplate(producerFactory)

    val participantProducerFactory = DefaultKafkaProducerFactory<Long, ParticipantAvro>(producerProps)
    participantKafkaTemplate = KafkaTemplate(participantProducerFactory)

    // RoomProducerImpl 생성
    roomProducer = RoomProducerImpl(
      roomKafkaTemplate = roomKafkaTemplate,
      participantKafkaTemplate = participantKafkaTemplate,
      roomMapper = roomMapper, // 필요 시 Mock으로 주입
      participantMapper = participantMapper // 필요 시 Mock으로 주입
    )

    val consumerProps = mutableMapOf<String, Any>(
      "bootstrap.servers" to embeddedKafka.brokersAsString,
      "group.id" to "test-group",
      "key.deserializer" to org.apache.kafka.common.serialization.LongDeserializer::class.java,
      "value.deserializer" to io.confluent.kafka.serializers.KafkaAvroDeserializer::class.java,
      "schema.registry.url" to "mock://embedded-schema-registry",
      "specific.avro.reader" to true
    )
    val roomConsumerFactory = DefaultKafkaConsumerFactory<Long, RoomAvro>(consumerProps)
    val participantConsumerFactory = DefaultKafkaConsumerFactory<Long, ParticipantAvro>(consumerProps)
    roomKafkaConsumer = roomConsumerFactory.createConsumer()
    roomKafkaConsumer.subscribe(listOf("room"))

    participantKafkaConsumer = participantConsumerFactory.createConsumer()
    participantKafkaConsumer.subscribe(listOf("participant"))

    roomService = RoomServiceImpl(
      roomRepository = roomRepository,
      roomProducer = roomProducer,
      roomMapper = roomMapper,
      participantMapper = participantMapper
    )
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
    val records = roomKafkaConsumer.poll(Duration.ofSeconds(10))
    assertThat(records.count()).isEqualTo(1)
    val record = records.iterator().next()
    assertThat(record.value().id).isEqualTo(roomResponseDTO.id)
    assertThat(record.value().roomName).isEqualTo(newRoomName)
  }
}