package vottega.room_service.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import vottega.avro.RoomAvro

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

  @Bean
  fun makeRoomTopic(): NewTopic {
    return NewTopic("room", 1, 1)
  }
}
