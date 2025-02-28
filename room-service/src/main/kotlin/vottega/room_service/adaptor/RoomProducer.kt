package vottega.room_service.adaptor

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import vottega.avro.Action
import vottega.avro.ParticipantAvro
import vottega.avro.RoomAvro
import vottega.room_service.dto.ParticipantResponseDTO
import vottega.room_service.dto.RoomResponseDTO
import vottega.room_service.dto.mapper.ParticipantMapper
import vottega.room_service.dto.mapper.RoomMapper


@Component
class RoomProducer(
  private val roomKafkaTemplate: KafkaTemplate<Long, RoomAvro>,
  private val participantKafkaTemplate: KafkaTemplate<Long, ParticipantAvro>,
  private val roomMapper: RoomMapper,
  private val participantMapper: ParticipantMapper,
) {
  fun roomEditMessageProduce(roomResponseDTO: RoomResponseDTO) {
    val roomOutAvro = roomMapper.toRoomAvro(roomResponseDTO)
    roomKafkaTemplate.send("room", roomOutAvro.id, roomOutAvro)
  }

  fun participantEditMessageProduce(participantResponseDTO: ParticipantResponseDTO, action: Action) {
    val participantAvro = participantMapper.toParticipantAvro(participantResponseDTO, action)
    participantKafkaTemplate.send("participant", participantAvro.roomId, participantAvro)
  }
}