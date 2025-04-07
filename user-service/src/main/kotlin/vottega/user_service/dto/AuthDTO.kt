package vottega.user_service.dto

data class AuthResponseDTO(
  val token: String
)

data class UserAuthRequestDTO(
  val id: Long,
  val userId: String,
)
