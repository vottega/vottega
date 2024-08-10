package vottega.room_service.service

import vottega.room_service.domain.Room
import vottega.room_service.domain.enumeration.RoomStatus
import vottega.room_service.dto.ParticipantInfoDTO
import vottega.room_service.dto.ParticipantRoleDTO
import java.util.*

interface RoomService {

    fun createRoom(roomName: String, ownerId : Long, participantInfoDTOS: List<ParticipantInfoDTO>) : Room
    fun updateRoom(roomId: Long, roomName: String? = null, status : RoomStatus? = null) : Room
    fun addParticipant(roomId: Long, participantInfoDTOS: List<ParticipantInfoDTO>): Room
    fun removeParticipant(roomId: Long, participantId: UUID): Room
    fun updateParticipant(roomId: Long, participantId: UUID, participantInfoDTO: ParticipantInfoDTO): Room
    fun enterParticipant(roomId: Long, participantId: UUID): Room
    fun exitParticipant(roomId: Long, participantId: UUID): Room

    fun addRole(roomId: Long, roleInfo: ParticipantRoleDTO): Room

    fun getRoom(roomId: Long): Room

}