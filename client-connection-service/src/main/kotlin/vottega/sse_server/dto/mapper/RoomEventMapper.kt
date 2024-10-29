package vottega.sse_server.dto.mapper

import org.springframework.stereotype.Component
import vottega.sse_server.dto.RoomEvent

@Component
class RoomEventMapper {
    fun voteDataToRoomEvent(data: Any) : RoomEvent = RoomEvent("voteInfo", 1, data) //TODO 변환 로직 구현
    fun shorthandDataToRoomEvent(data: Any) : RoomEvent = RoomEvent("shorthandInfo", 1, data) //TODO 변환 로직 구현
    fun roomDataToRoomEvent(data: Any) : RoomEvent = RoomEvent("roomInfo", 1, data) //TODO 변환 로직 구현
}