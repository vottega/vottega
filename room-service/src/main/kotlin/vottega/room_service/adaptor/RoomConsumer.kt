package vottega.room_service.adaptor

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class RoomConsumer(
) {
    @KafkaListener(topics = ["room"])
    fun enterEventConsume(message: String) {
        //사람 입장 이벤트 처리

    }

    @KafkaListener(topics = ["room"])
    fun exitEventConsume(message: String) {
        //사람 퇴장 이벤트 처리

    }
}