package com.example.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import vottega.room_service.avro.ParticipantAvro
import vottega.room_service.config.KafkaCommonConfig

@EnableKafka
@Configuration
class ParticipantKafkaConsumerConfig(
  private val kafkaCommonConfig: KafkaCommonConfig
) {

  @Bean
  fun participantConsumerFactory(): ConsumerFactory<Long, ParticipantAvro> {
    return DefaultKafkaConsumerFactory(
      kafkaCommonConfig.commonConsumerConfig()
    )
  }

  @Bean
  fun participantKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<Long, ParticipantAvro> {
    val factory = ConcurrentKafkaListenerContainerFactory<Long, ParticipantAvro>()
    factory.consumerFactory = participantConsumerFactory()
    return factory
  }
}
