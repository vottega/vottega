package vottega.room_service.adaptor.impl

import main.room_service.avro.RoomAvro
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import vottega.room_service.adaptor.RoomProducer
import vottega.room_service.avro.ParticipantAvro
import vottega.room_service.dto.ParticipantResponseDTO
import vottega.room_service.dto.RoomResponseDTO
import vottega.room_service.dto.mapper.ParticipantMapper
import vottega.room_service.dto.mapper.RoomMapper

@Component
class RoomProducerImpl(
  private val roomKafkaTemplate: KafkaTemplate<Long, RoomAvro>,
  private val participantKafkaTemplate: KafkaTemplate<Long, ParticipantAvro>,
  private val roomMapper: RoomMapper,
  private val participantMapper: ParticipantMapper,
) : RoomProducer {
  override fun roomEditMessageProduce(roomResponseDTO: RoomResponseDTO) {
    val roomOutAvro = roomMapper.toRoomAvro(roomResponseDTO)
    roomKafkaTemplate.send("room", roomOutAvro.id, roomOutAvro)
  }

  override fun participantEditMessageProduce(participantResponseDTO: ParticipantResponseDTO) {
    val participantAvro = participantMapper.toParticipantAvro(participantResponseDTO)
    participantKafkaTemplate.send("participant", participantAvro.roomId, participantAvro)
  }
}