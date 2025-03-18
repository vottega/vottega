package vottega.user_service.dto

data class UserCreateRequest(
  val name: String,
  val userId: String,
  val password: String,
  val email: String,
  val emailAuthCode: String,
)