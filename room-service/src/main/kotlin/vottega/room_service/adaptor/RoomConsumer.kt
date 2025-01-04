package vottega.room_service.adaptor

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import vottega.room_service.service.RoomService

@Service
class RoomConsumer(
  val roomService: RoomService
) {
  @KafkaListener(
    topics = ["participant-out"],
    groupId = "room-service",
    containerFactory = 'participantKafkaListenerContainerFactory'
  ) // todo 변수 다른데 저장
  fun participantConnectEventConsume(data: ParticipantConnectData) {
    if (data.action == 'ENTER') { // TODO 개발하기
      roomService.participantEnter(data.roomId, data.participantId)
    } else {
      roomService.participantExit(data.roomId, data.participantId)
    }
    //카프카 좀 더 배워서 실행
  }

}