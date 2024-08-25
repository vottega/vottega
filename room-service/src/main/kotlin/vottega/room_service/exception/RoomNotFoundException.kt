package vottega.room_service.exception

import java.lang.RuntimeException

class RoomNotFoundException(roomId: Long) : RuntimeException("$roomId room not found")