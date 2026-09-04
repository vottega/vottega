package vottega.sse_server.dto

import vottega.sse_server.dto.mapper.RoomStatus
import java.time.LocalDateTime

data class RoomResponseDTO(
  val id: Long,
  val name: String,
  val ownerId: Long,
  val status: RoomStatus,
  val roles: List<ParticipantRoleDTO>,
  val createdAt: LocalDateTime,
  val lastUpdatedAt: LocalDateTime,
  val startedAt: LocalDateTime?,
  val finishedAt: LocalDateTime?,
)
