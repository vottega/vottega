package vottega.user_service.dto

data class AuthResponseDTO(
  val verified: Boolean,
  val id: Long? = null,
)
