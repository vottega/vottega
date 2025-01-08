package vottega.room_service.dto


data class ParticipantInfoDTO(
  val name: String,
  val phoneNumber: String? = null,
  val position: String? = null,
  val role: String? = null,
)
