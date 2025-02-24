package vottega.sse_server.dto.mapper

import org.springframework.stereotype.Component
import vottega.sse_server.avro.Action
import vottega.sse_server.avro.ParticipantAvro
import vottega.sse_server.dto.ParticipantResponseDTO
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


@Component
class ParticipantMapper(
  private val participantRoleMapper: ParticipantRoleMapper,
) {
  fun toParticipantResponseDTO(participant: ParticipantAvro): ParticipantResponseDTO {
    return ParticipantResponseDTO(
      id = participant.id,
      name = participant.name,
      roomId = participant.roomId,
      position = participant.position,
      participantRole = participantRoleMapper.toParticipantRoleDTO(participant.role),
      isEntered = participant.isEntered,
      createdAt = participant.createdAt.atZone(ZoneId.systemDefault()).toLocalDateTime(),
      enteredAt = participant.enteredAt.atZone(ZoneId.systemDefault()).toLocalDateTime(),
      lastUpdatedAt = participant.lastUpdatedAt.atZone(ZoneId.systemDefault()).toLocalDateTime()
    )
  }

  fun toParticipantAvro(roomId: Long, participantId: UUID, roomAction: Action): ParticipantAvro {
    return ParticipantAvro.newBuilder()
      .setId(participantId)
      .setRoomId(roomId)
      .setAction(roomAction)
      .setCreatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
      .build()
  }
}