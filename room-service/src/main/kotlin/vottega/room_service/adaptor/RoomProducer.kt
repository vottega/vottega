package vottega.room_service.adaptor

import org.springframework.stereotype.Component
import vottega.room_service.dto.ParticipantResponseDTO
import vottega.room_service.dto.RoomResponseDTO


@Component
interface RoomProducer {

  fun roomEditMessageProduce(roomResponseDTO: RoomResponseDTO)
  fun participantEditMessageProduce(participantResponseDTO: ParticipantResponseDTO)

}