package vottega.room_service.exception

import vottega.room_service.domain.enumeration.RoomStatus

class RoomStatusConflictException(status: RoomStatus) : RuntimeException("$status : Conflict RoomStatus Request")