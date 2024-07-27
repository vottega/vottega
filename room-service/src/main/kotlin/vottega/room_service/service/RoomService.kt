package vottega.room_service.service

import vottega.room_service.domain.Room
import vottega.room_service.domain.enumeration.ParticipantRole
import vottega.room_service.dto.participantInfoDTO
import java.util.*

interface RoomService {

    fun createRoom(roomName: String, ownerId : Long, participantInfoDTOs: List<participantInfoDTO>) : Room
    fun renameRoom(roomId: Long, roomName: String) : Room
    fun startRoom(roomId: Long) : Room
    fun finishRoom(roomId: Long) : Room
    fun addParticipant(roomId: Long, name: String, position: String, role: ParticipantRole, canVote: Boolean): Room
    fun removeParticipant(roomId: Long, participantId: UUID): Room
    fun updateParticipant(roomId: Long, participantId: UUID, name: String, position: String, role: ParticipantRole, canVote: Boolean): Room
    fun enterParticipant(roomId: Long, participantId: UUID): Room
    fun exitParticipant(roomId: Long, participantId: UUID): Room

}