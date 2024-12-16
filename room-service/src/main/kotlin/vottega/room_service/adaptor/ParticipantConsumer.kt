package vottega.room_service.adaptor

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import vottega.room_service.service.RoomService

@Component
class ParticipantConsumer(val roomService: RoomService) {
    @KafkaListener(topics = ["participant-out"], groupId = "room-service")
    fun participantConnectEventConsume(data: ParticipantConnectData) {
        if(data.action == 'ENTER'){
            roomService.enterParticipant(data.roomId, data.participantId)
        } else {
            roomService.exitParticipant(data.roomId, data.participantId)
        }
    }
}