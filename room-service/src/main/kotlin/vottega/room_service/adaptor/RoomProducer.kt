package vottega.room_service.adaptor

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service


@Service
class KafkaProducer(private val kafkaTemplate: KafkaTemplate<String, String>) {

    fun roomEditMessageProduce(topic: String, message: String) {
        kafkaTemplate.send(topic, message)
    }


}