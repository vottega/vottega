package vottega.auth_server.dto;

import java.util.*

data class VerifyResponseDTO(
    val role: Role,
    val participantId: UUID?,
    val roomId: Long?,
    val userId: Long?
)
