package vottega.sse_server.dto

data class RoomEvent(
  val type: RoomEventType,
  val data: Any
)

enum class RoomEventType {
  ROOM_INFO, PARTICIPANT_INFO, VOTE_INFO, VOTE_PAPER_INFO
}