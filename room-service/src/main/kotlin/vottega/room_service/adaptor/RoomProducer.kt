package vottega.room_service.adaptor

import org.springframework.stereotype.Service
import vottega.room_service.dto.ParticipantResponseDTO
import vottega.room_service.dto.RoomResponseDTO


@Service
interface RoomProducer {

  fun roomEditMessageProduce(roomResponseDTO: RoomResponseDTO)
  fun participantEditMessageProduce(participantResponseDTO: ParticipantResponseDTO)

}