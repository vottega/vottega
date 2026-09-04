package vottega.user_service.dto

data class EmailValidateRequest(
  val email: String,
  val emailAuthCode: String,
)

data class EmailSendRequest(
  val email: String,
)