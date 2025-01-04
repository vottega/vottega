package com.example.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import vottega.room_service.domain.Room

@EnableKafka
@Configuration
class RoomKafkaConsumerConfig {

  @Bean
  fun roomConsumerFactory(): ConsumerFactory<String, Room> {
    val config = mutableMapOf<String, Any>(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
      ConsumerConfig.GROUP_ID_CONFIG to "room-group",
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to "io.confluent.kafka.serializers.KafkaAvroDeserializer",
      "schema.registry.url" to "http://localhost:8081",
      "specific.avro.reader" to true
    )
    return DefaultKafkaConsumerFactory(config)
  }

  @Bean
  fun roomKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Room> {
    val factory = ConcurrentKafkaListenerContainerFactory<String, Room>()
    factory.consumerFactory = roomConsumerFactory()
    return factory
  }
}
