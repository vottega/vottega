package vottega.sse_server.config

import com.fasterxml.jackson.databind.ser.std.StringSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions

@Configuration
class KafkaSenderConfig {

    @Bean
    fun kafkaSender(): KafkaSender<String, String> {
        // Kafka 프로듀서 설정
        val producerProps = mapOf<String, Any>(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.ACKS_CONFIG to "1" // 필요한 경우 설정 조정
        )

        val senderOptions = SenderOptions.create<String, String>(producerProps)
        return KafkaSender.create(senderOptions)
    }
}