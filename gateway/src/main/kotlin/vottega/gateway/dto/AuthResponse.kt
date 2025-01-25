package vottega.gateway.dto

import java.util.*

data class AuthResponse(
  val role : Role,
  val participantId : UUID?,
  val roomId : Long?,
  val userId : Long?,
)