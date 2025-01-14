package vottega.room_service.service

import vottega.room_service.domain.enumeration.RoomStatus
import vottega.room_service.dto.ParticipantInfoDTO
import vottega.room_service.dto.ParticipantRoleDTO
import vottega.room_service.dto.RoomResponseDTO
import java.util.*

interface RoomService {

  fun createRoom(
    roomName: String,
    ownerId: Long,
    participantRoleDTOList: List<ParticipantRoleDTO>,
  ): RoomResponseDTO

  fun updateRoom(roomId: Long, roomName: String? = null, status: RoomStatus? = null): RoomResponseDTO
  fun addParticipant(roomId: Long, participantInfoDTOS: List<ParticipantInfoDTO>): RoomResponseDTO
  fun removeParticipant(roomId: Long, participantId: UUID): RoomResponseDTO
  fun updateParticipant(roomId: Long, participantId: UUID, participantInfoDTO: ParticipantInfoDTO): RoomResponseDTO
  fun enterParticipant(roomId: Long, participantId: UUID): RoomResponseDTO
  fun exitParticipant(roomId: Long, participantId: UUID): RoomResponseDTO

  fun addRole(roomId: Long, roleInfo: ParticipantRoleDTO): RoomResponseDTO

  fun deleteRole(roomId: Long, role: String): RoomResponseDTO

  fun getRoom(roomId: Long): RoomResponseDTO

  fun getRoomList(userId: Long): List<RoomResponseDTO>

}