package vottega.room_service.service.impl

import main.room_service.avro.RoomAvro
import org.apache.kafka.clients.consumer.Consumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import vottega.room_service.adaptor.RoomProducer
import vottega.room_service.adaptor.impl.RoomProducerImpl
import vottega.room_service.avro.ParticipantAvro
import vottega.room_service.dto.mapper.ParticipantMapper
import vottega.room_service.dto.mapper.RoomMapper
import vottega.room_service.repository.RoomRepository
import vottega.room_service.service.RoomService

@TestConfiguration
@EmbeddedKafka(
  partitions = 1,
  topics = ["room", "participant"]
)
class TestConfig {
  @Autowired
  private lateinit var embeddedKafka: EmbeddedKafkaBroker

  @Autowired
  private lateinit var roomMapper: RoomMapper

  @Autowired
  private lateinit var participantMapper: ParticipantMapper

  @Autowired
  private lateinit var roomRepository: RoomRepository


  @Bean
  @Primary
  fun roomProducer(): RoomProducer {
    return RoomProducerImpl(
      roomKafkaTemplate = getKafkaTemplate<RoomAvro>(),
      participantKafkaTemplate = getKafkaTemplate<ParticipantAvro>(),
      roomMapper = roomMapper,
      participantMapper = participantMapper,
    )
  }

  @Bean
  @Primary
  fun roomKafKaConsumer(): Consumer<Long, RoomAvro> {
    return getKafkaConsumer<RoomAvro>("room")
  }

  @Bean
  @Primary
  fun participantKafkaConsumer(): Consumer<Long, ParticipantAvro> {
    return getKafkaConsumer<ParticipantAvro>("participant")
  }

  private fun <T> getKafkaTemplate(): KafkaTemplate<Long, T> {
    val producerProps = mutableMapOf<String, Any>(
      "bootstrap.servers" to embeddedKafka.brokersAsString,
      "key.serializer" to org.apache.kafka.common.serialization.LongSerializer::class.java,
      "value.serializer" to io.confluent.kafka.serializers.KafkaAvroSerializer::class.java,
      "schema.registry.url" to "mock://embedded-schema-registry"
    )
    val producerFactory = DefaultKafkaProducerFactory<Long, T>(producerProps)
    return KafkaTemplate(producerFactory)
  }

  private fun <T> getKafkaConsumer(topic: String): Consumer<Long, T> {
    val consumerProps = mutableMapOf<String, Any>(
      "bootstrap.servers" to embeddedKafka.brokersAsString,
      "group.id" to "test-group",
      "key.deserializer" to org.apache.kafka.common.serialization.LongDeserializer::class.java,
      "value.deserializer" to io.confluent.kafka.serializers.KafkaAvroDeserializer::class.java,
      "schema.registry.url" to "mock://embedded-schema-registry",
      "specific.avro.reader" to true
    )
    val consumerFactory = DefaultKafkaConsumerFactory<Long, T>(consumerProps)
    val consumer = consumerFactory.createConsumer()
    consumer.subscribe(listOf(topic))
    return consumer
  }

  @Bean
  @Primary
  fun roomService(): RoomService {
    return RoomServiceImpl(
      roomRepository = roomRepository,
      roomProducer = roomProducer(),
      roomMapper = roomMapper,
      participantMapper = participantMapper
    )
  }
}