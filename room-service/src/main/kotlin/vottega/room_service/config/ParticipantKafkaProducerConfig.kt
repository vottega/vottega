package com.example.config

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import vottega.room_service.domain.Participant

@Configuration
class ParticipantKafkaProducerConfig {

  @Bean
  fun participantProducerFactory(): ProducerFactory<String, Participant> {
    val config = mutableMapOf<String, Any>(
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name,
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to "io.confluent.kafka.serializers.KafkaAvroSerializer",
      "schema.registry.url" to "http://localhost:8081"
    )
    return DefaultKafkaProducerFactory(config)
  }

  @Bean
  fun participantKafkaTemplate(): KafkaTemplate<String, Participant> {
    return KafkaTemplate(participantProducerFactory())
  }
}
