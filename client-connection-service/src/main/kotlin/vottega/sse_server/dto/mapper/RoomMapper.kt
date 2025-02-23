package vottega.sse_server.dto.mapper

import org.springframework.stereotype.Component
import vottega.sse_server.avro.RoomAvro
import vottega.sse_server.avro.Status
import vottega.sse_server.dto.RoomResponseDTO
import java.time.ZoneId

@Component
class RoomMapper(
  private val participantRoleMapper: ParticipantRoleMapper,
) {
  fun toRoomDTO(roomAvro: RoomAvro): RoomResponseDTO {
    return RoomResponseDTO(
      id = roomAvro.id,
      name = roomAvro.roomName,
      ownerId = roomAvro.ownerId,
      status = statusToRoomStatus(roomAvro.status),
      roles = roomAvro.participantRoleList.map { participantRoleMapper.toParticipantRoleDTO(it) },
      createdAt = roomAvro.createdAt.atZone(ZoneId.systemDefault()).toLocalDateTime(),
      lastUpdatedAt = roomAvro.lastUpdatedAt.atZone(ZoneId.systemDefault()).toLocalDateTime(),
      startedAt = roomAvro.startedAt?.atZone(ZoneId.systemDefault())?.toLocalDateTime(),
      finishedAt = roomAvro.finishedAt?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
    )
  }

  private fun statusToRoomStatus(status: Status): RoomStatus {
    return when (status) {
      Status.NOT_STARTED -> RoomStatus.NOT_STARTED
      Status.STOPPED -> RoomStatus.STOPPED
      Status.PROGRESS -> RoomStatus.PROGRESS
      Status.FINISHED -> RoomStatus.FINISHED
    }
  }
}

enum class RoomStatus {
  NOT_STARTED,
  PROGRESS,
  FINISHED,
  STOPPED
}