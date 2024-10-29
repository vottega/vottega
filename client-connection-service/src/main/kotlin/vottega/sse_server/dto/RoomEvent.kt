package vottega.sse_server.dto

data class RoomEvent(
    val type: String, // "roomInfo", "voteInfo", "shorthandInfo"
    val roomId: Long,
    val data: Any
)