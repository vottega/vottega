package vottega.room_service.dto

data class CreateRoomRequestDTO(
  val roomName: String,
  val ownerId: Long,
  val participantRoleList: List<ParticipantRoleDTO>,
)