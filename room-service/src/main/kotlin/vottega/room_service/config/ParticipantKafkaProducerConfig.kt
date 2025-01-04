package com.example.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import vottega.room_service.avro.ParticipantAvro
import vottega.room_service.config.KafkaCommonConfig

@Configuration
class ParticipantKafkaProducerConfig(
  private val kafkaCommonConfig: KafkaCommonConfig
) {

  @Bean
  fun participantProducerFactory(): ProducerFactory<Long, ParticipantAvro> {
    return DefaultKafkaProducerFactory(kafkaCommonConfig.commonProducerConfig())
  }

  @Bean
  fun participantKafkaTemplate(): KafkaTemplate<Long, ParticipantAvro> {
    return KafkaTemplate(participantProducerFactory())
  }
}
