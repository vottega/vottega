package com.example.config

import main.room_service.avro.RoomAvro
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import vottega.room_service.config.KafkaCommonConfig

@Configuration
class RoomKafkaProducerConfig(
  private val kafkaCommonConfig: KafkaCommonConfig
) {

  @Bean
  fun roomProducerFactory(): ProducerFactory<Long, RoomAvro> {
    return DefaultKafkaProducerFactory(kafkaCommonConfig.commonProducerConfig())
  }

  @Bean
  fun roomKafkaTemplate(): KafkaTemplate<Long, RoomAvro> {
    return KafkaTemplate(roomProducerFactory())
  }
}
