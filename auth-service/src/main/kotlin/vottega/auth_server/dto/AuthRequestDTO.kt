package vottega.auth_server.dto;

import java.util.UUID

data class ParticipantAuthRequestDTO(
    val participantId: UUID
)

data class UserAuthRequestDTO(
    val id: Long,
    val userId: String,
)
