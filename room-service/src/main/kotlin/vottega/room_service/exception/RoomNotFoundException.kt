package vottega.room_service.exception

class RoomNotFoundException(roomId: Long) : RuntimeException("$roomId room not found")