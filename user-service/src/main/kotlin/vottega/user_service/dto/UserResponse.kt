package vottega.user_service.dto

data class UserResponse(
  val id: Long,
  val username: String,
  val userId: String,
  val email: String
)