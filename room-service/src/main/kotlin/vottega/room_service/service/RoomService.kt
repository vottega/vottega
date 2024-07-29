package vottega.room_service.service

import vottega.room_service.domain.Room
import vottega.room_service.domain.enumeration.ParticipantRole
import vottega.room_service.domain.enumeration.RoomStatus
import vottega.room_service.dto.participantInfoDTO
import java.util.*

interface RoomService {

    fun createRoom(roomName: String, ownerId : Long, participantInfoDTOs: List<participantInfoDTO>) : Room
    fun updateRoom(roomId: Long, roomName: String? = null, status : RoomStatus? = null) : Room
    fun addParticipant(roomId: Long, participantInfoDTOs: List<participantInfoDTO>): Room
    fun removeParticipant(roomId: Long, participantId: UUID): Room
    fun updateParticipant(roomId: Long, participantId: UUID, participantInfoDTO: participantInfoDTO): Room
    fun enterParticipant(roomId: Long, participantId: UUID): Room
    fun exitParticipant(roomId: Long, participantId: UUID): Room

    fun getRoom(roomId: Long): Room

}