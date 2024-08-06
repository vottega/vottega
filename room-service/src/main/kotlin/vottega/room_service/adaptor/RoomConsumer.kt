package vottega.room_service.adaptor

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class RoomConsumer(
) {
    @KafkaListener(topics = ["room"])
    fun participantConnectEventConsume(message: String) {
        //TODO 사람 입장 및 퇴장 이벤트 처리 (From Connection Service)
        
    }
}