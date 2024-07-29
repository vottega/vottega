package vottega.room_service.dto

import vottega.room_service.domain.enumeration.RoomStatus

data class UpdateRoomRequestDTO(val roomName : String? = null,  val status : RoomStatus? = null)
