package vottega.sse_server.dto.mapper

import org.springframework.stereotype.Component
import vottega.avro.Action
import vottega.avro.ParticipantAvro
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
      participantRole = participant.role?.let { participantRoleMapper.toParticipantRoleDTO(it) },
      isEntered = participant.isEntered,
      createdAt = participant.createdAt.atZone(ZoneId.systemDefault()).toLocalDateTime(),
      enteredAt = participant.enteredAt?.atZone(ZoneId.systemDefault())?.toLocalDateTime(),
      lastUpdatedAt = participant.lastUpdatedAt?.atZone(ZoneId.systemDefault())?.toLocalDateTime(),
      action = avroActionToAction(participant.action)
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

  fun avroActionToAction(action: Action): vottega.sse_server.dto.Action {
    return when (action) {
      Action.ENTER -> vottega.sse_server.dto.Action.ENTER
      Action.EXIT -> vottega.sse_server.dto.Action.EXIT
      Action.EDIT -> vottega.sse_server.dto.Action.EDIT
      Action.ADD -> vottega.sse_server.dto.Action.ADD
      Action.DELETE -> vottega.sse_server.dto.Action.DELETE
    }
  }
}
