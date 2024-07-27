package vottega.room_service.dto

import vottega.room_service.domain.enumeration.RoomStatus

data class RoomRequestDTO(val roomName : String? = null, val participants : List<participantInfoDTO>? = null,val status : RoomStatus? = null)
