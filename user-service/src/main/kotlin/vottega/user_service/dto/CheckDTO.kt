package vottega.user_service.dto

data class EmailCheckRequest(
  val email: String
)

data class UserIdCheckRequest(
  val userId: String
)

data class LoginRequest(
  val userId: String,
  val password: String
)