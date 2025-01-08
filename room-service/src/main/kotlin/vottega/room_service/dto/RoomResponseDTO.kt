package vottega.room_service.dto

import vottega.room_service.domain.enumeration.RoomStatus
import java.time.LocalDateTime

data class RoomResponseDTO(
  val id: Long,
  val name: String,
  val ownerId: Long,
  val status: RoomStatus,
  val participants: List<ParticipantResponseDTO>,
  val createdAt: LocalDateTime,
  val lastUpdatedAt: LocalDateTime,
  val startedAt: LocalDateTime?,
  val finishedAt: LocalDateTime?,
)
