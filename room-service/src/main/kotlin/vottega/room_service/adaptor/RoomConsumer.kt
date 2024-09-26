package vottega.room_service.adaptor

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import vottega.room_service.service.RoomService

@Service
class RoomConsumer(val roomService: RoomService
) {
    @KafkaListener(topics = ["room"])
    fun participantConnectEventConsume(message: String) {
        //TODO 사람 입장 및 퇴장 이벤트 처리 (From Connection Service)
        roomService.exitParticipant(message)
        roomService.enterParticipant(message)
        //카프카 좀 더 배워서 실행
    }
}