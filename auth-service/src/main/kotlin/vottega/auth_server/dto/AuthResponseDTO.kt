package vottega.auth_server.dto


data class AuthResponseDTO(
  val token: String,
)

data class ParticipantAuthResponseDTO(
  val token: String,
  val roomId: Long,
)