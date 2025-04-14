//package vottega.room_service.service
//
//import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient
//import io.confluent.kafka.serializers.KafkaAvroSerializer
//import main.room_service.avro.RoomAvro
//import org.apache.kafka.clients.consumer.Consumer
//import org.apache.kafka.clients.producer.ProducerConfig
//import org.apache.kafka.common.serialization.LongSerializer
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.TestConfiguration
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Primary
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory
//import org.springframework.kafka.core.DefaultKafkaProducerFactory
//import org.springframework.kafka.core.KafkaTemplate
//import org.springframework.kafka.core.ProducerFactory
//import org.springframework.kafka.test.EmbeddedKafkaBroker
//import vottega.room_service.adaptor.RoomProducer
//import vottega.room_service.adaptor.impl.RoomProducerImpl
//import vottega.room_service.avro.ParticipantAvro
//import vottega.room_service.dto.mapper.ParticipantMapper
//import vottega.room_service.dto.mapper.RoomMapper
//import vottega.room_service.repository.RoomRepository
//
//@TestConfiguration
//class TestConfig {
//  @Autowired
//  private lateinit var embeddedKafka: EmbeddedKafkaBroker
//
//  @Autowired
//  private lateinit var roomMapper: RoomMapper
//
//  @Autowired
//  private lateinit var participantMapper: ParticipantMapper
//
//  @Autowired
//  private lateinit var roomRepository: RoomRepository
//
//  @Bean
//  fun mockSchemaRegistryClient(): MockSchemaRegistryClient = MockSchemaRegistryClient()
//
//
//  @Bean
//  @Primary
//  fun roomProducer(): RoomProducer {
//    return RoomProducerImpl(
//      roomKafkaTemplate = getKafkaTemplate<RoomAvro>(),
//      participantKafkaTemplate = getKafkaTemplate<ParticipantAvro>(),
//      roomMapper = roomMapper,
//      participantMapper = participantMapper,
//    )
//  }
//
//  @Bean
//  @Primary
//  fun roomKafKaConsumer(): Consumer<Long, RoomAvro> {
//    return getKafkaConsumer<RoomAvro>("room")
//  }
//
//  @Bean
//  @Primary
//  fun participantKafkaConsumer(): Consumer<Long, ParticipantAvro> {
//    return getKafkaConsumer<ParticipantAvro>("participant")
//  }
//
//
//  @Suppress("UNCHECKED_CAST")
//  private fun <T> getKafkaTemplate(
//    mockSchemaRegistryClient: MockSchemaRegistryClient
//  ): KafkaTemplate<Long, T> {
//    val producerProps = mutableMapOf<String, Any>(
//      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to embeddedKafka.brokersAsString,
//      // 여기서는 실제 서버가 아니라 "mock" URL이라고 명시하지만, 로컬에선 MockSchemaRegistryClient가 해결
//      "schema.registry.url" to "mock://embedded-schema-registry",
//      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to LongSerializer::class.java.name,
//      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to KafkaAvroSerializer::class.java.name,
//      // 추가로 필요할 수 있는 설정들
//    )
//
//    // Serializer에 직접 MockSchemaRegistryClient를 주입
//    val keySerializer = LongSerializer()
//    val valueSerializer = KafkaAvroSerializer(mockSchemaRegistryClient)
//
//    // 위 설정으로 ProducerFactory 생성
//    val producerFactory: ProducerFactory<Long, T> =
//      DefaultKafkaProducerFactory(producerProps, keySerializer, valueSerializer)
//
//    return KafkaTemplate(producerFactory)
//  }
//
//  private fun <T> getKafkaConsumer(topic: String): Consumer<Long, T> {
//    val consumerProps = mutableMapOf<String, Any>(
//      "bootstrap.servers" to embeddedKafka.brokersAsString,
//      "group.id" to "test-group",
//      "key.deserializer" to org.apache.kafka.common.serialization.LongDeserializer::class.java,
//      "value.deserializer" to io.confluent.kafka.serializers.KafkaAvroDeserializer::class.java,
//      "schema.registry.url" to "mock://embedded-schema-registry",
//      "specific.avro.reader" to true
//    )
//    val consumerFactory = DefaultKafkaConsumerFactory<Long, T>(consumerProps)
//    val consumer = consumerFactory.createConsumer()
//    consumer.subscribe(listOf(topic))
//    return consumer
//  }
//
//  @Bean
//  @Primary
//  fun roomService(): RoomService {
//    return RoomServiceImpl(
//      roomRepository = roomRepository,
//      roomProducer = roomProducer(),
//      roomMapper = roomMapper,
//      participantMapper = participantMapper
//    )
//  }
//
//  @Bean
//  @Primary
//  fun getEmbeddedKafkaBroker(): EmbeddedKafkaBroker {
//    return embeddedKafka
//  }
//}