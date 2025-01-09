package vottega.room_service.adaptor

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import vottega.room_service.avro.Action
import vottega.room_service.avro.ParticipantAvro
import vottega.room_service.service.RoomService

@Component
class ParticipantConsumer(
  val roomService: RoomService,
) {
  @KafkaListener(
    topics = ["participant"],
    groupId = "\${spring.kafka.consumer.group-id}",
    containerFactory = "participantKafkaListenerContainerFactory"
  )
  fun participantConnectEventConsume(participantAvro: ParticipantAvro) {
    if (participantAvro.action == Action.EXIT) {
      roomService.exitParticipant(participantId = participantAvro.id, roomId = participantAvro.roomId)
    } else if (participantAvro.action == Action.ENTER) {
      roomService.enterParticipant(participantId = participantAvro.id, roomId = participantAvro.roomId)
    }
  }
}