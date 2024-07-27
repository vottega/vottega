package vottega.room_service.dto

import vottega.room_service.domain.enumeration.ParticipantRole
import java.time.LocalDateTime
import java.util.*

data class ParticipantResponseDTO(
    val id: UUID,
    val name: String,
    val position: String,
    val role: ParticipantRole,
    val canVote: Boolean,
    val createdAt: LocalDateTime,
    val enteredAt: LocalDateTime?,
    val lastUpdatedAt: LocalDateTime,
)
