package com.example.config

import main.room_service.avro.RoomAvro
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import vottega.room_service.config.KafkaCommonConfig

@EnableKafka
@Configuration
class RoomKafkaConsumerConfig(
  private val kafkaCommonConfig: KafkaCommonConfig
) {

  @Bean
  fun roomConsumerFactory(): ConsumerFactory<Long, RoomAvro> {
    return DefaultKafkaConsumerFactory(kafkaCommonConfig.commonConsumerConfig())
  }

  @Bean
  fun roomKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<Long, RoomAvro> {
    val factory = ConcurrentKafkaListenerContainerFactory<Long, RoomAvro>()
    factory.consumerFactory = roomConsumerFactory()
    return factory
  }
}
