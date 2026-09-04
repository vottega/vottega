package vottega.sse_server.dto

import java.time.LocalDateTime
import java.util.*

data class ParticipantResponseDTO(
  val id: UUID,
  val name: String?,
  val roomId: Long,
  val position: String?,
  val participantRole: ParticipantRoleDTO?,
  val isEntered: Boolean?,
  val createdAt: LocalDateTime,
  val enteredAt: LocalDateTime?,
  val lastUpdatedAt: LocalDateTime?,
  val action: Action
)

enum class Action {
  ENTER, EXIT, EDIT, ADD, DELETE
}

data class ParticipantExitEnterDTO(
  val id: UUID,
  val roomId: Long,
  val createdAt: LocalDateTime,
)