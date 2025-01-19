package vottega.room_service.service.impl

import jakarta.transaction.Transactional
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import vottega.room_service.adaptor.RoomProducer
import vottega.room_service.domain.Room
import vottega.room_service.domain.enumeration.RoomStatus
import vottega.room_service.dto.ParticipantInfoDTO
import vottega.room_service.dto.ParticipantRoleDTO
import vottega.room_service.dto.RoomResponseDTO
import vottega.room_service.dto.mapper.ParticipantMapper
import vottega.room_service.dto.mapper.RoomMapper
import vottega.room_service.exception.RoomNotFoundException
import vottega.room_service.repository.RoomRepository
import vottega.room_service.service.RoomService
import java.util.*

@Service
@Transactional
class RoomServiceImpl(
  private val roomRepository: RoomRepository,
  private val roomMapper: RoomMapper,
  private val roomProducer: RoomProducer,
  private val participantMapper: ParticipantMapper
) : RoomService {

  @PreAuthorize("hasRole('USER')")
  override fun createRoom(
    roomName: String,
    ownerId: Long,
    participantRoleDTOList: List<ParticipantRoleDTO>,
  ): RoomResponseDTO {
    val room = Room(roomName, ownerId)
    roomRepository.save(room)
    participantRoleDTOList.forEach { room.addParticipantRole(it.role, it.canVote) }

    return roomMapper.toRoomOutDTO(room)
  }


  @PreAuthorize("@roomSecurity.isOwner(authentication.principal, authentication.credentials)")
  override fun updateRoom(roomId: Long, roomName: String?, status: RoomStatus?): RoomResponseDTO {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    room.update(roomName, status)
    val roomDTO = roomMapper.toRoomOutDTO(room)
    roomProducer.roomEditMessageProduce(roomDTO)
    return roomDTO
  }


  @PreAuthorize("@roomSecurity.isOwner(authentication.principal, authentication.credentials)")
  override fun addParticipant(roomId: Long, participantInfoDTOS: List<ParticipantInfoDTO>): RoomResponseDTO {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    participantInfoDTOS.forEach {
      room.addParticipant(it)
    }
    roomRepository.save(room)
    participantInfoDTOS.forEach { participantInfoDTO ->
      room.participantList.find { participantInfoDTO.name == it.name }?.let { participant ->
        roomProducer.participantEditMessageProduce(participantMapper.toParticipantResponseDTO(participant))
      }
    }
    return roomMapper.toRoomOutDTO(room)
  }


  @PreAuthorize("@roomSecurity.isOwner(authentication.principal, authentication.credentials)")
  override fun removeParticipant(roomId: Long, participantId: UUID): RoomResponseDTO {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    room.apply { removeParticipant(participantId) }
    return roomMapper.toRoomOutDTO(room)
  }


  @PreAuthorize("@roomSecurity.isOwner(authentication.principal, authentication.credentials)")
  override fun updateParticipant(
    roomId: Long,
    participantId: UUID,
    participantInfoDTO: ParticipantInfoDTO
  ): RoomResponseDTO {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    val updatedParticipant = room.updateParticipant(participantId, participantInfoDTO)
    val participantDTO = participantMapper.toParticipantResponseDTO(updatedParticipant)
    roomProducer.participantEditMessageProduce(participantDTO)
    return roomMapper.toRoomOutDTO(room)
  }


  override fun enterParticipant(roomId: Long, participantId: UUID): RoomResponseDTO {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    room.apply { enterParticipant(participantId) }
    return roomMapper.toRoomOutDTO(room)
  }

  override fun exitParticipant(roomId: Long, participantId: UUID): RoomResponseDTO {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    room.apply { exitParticipant(participantId) }
    return roomMapper.toRoomOutDTO(room)
  }


  @PreAuthorize("@roomSecurity.isOwner(authentication.principal, authentication.credentials)")
  override fun addRole(roomId: Long, roleInfo: ParticipantRoleDTO): RoomResponseDTO {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    room.apply { addParticipantRole(roleInfo.role, roleInfo.canVote) }
    return roomMapper.toRoomOutDTO(room)
  }


  @PreAuthorize("@roomSecurity.isOwner(authentication.principal, authentication.credentials)")
  override fun deleteRole(roomId: Long, role: String): RoomResponseDTO {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    room.apply { deleteParticipantRole(role) }
    return roomMapper.toRoomOutDTO(room)
  }

  @PreAuthorize("@roomSecurity.isParticipantInRoomAndCanVote(authentication.principal, authentication.credentials, false )")
  override fun getRoom(roomId: Long): RoomResponseDTO {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    return roomMapper.toRoomOutDTO(room)
  }

  @PreAuthorize("@roomSecurity.isOwner(authentication.principal, authentication.credentials)")
  override fun getRoomList(userId: Long): List<RoomResponseDTO> {
    return roomRepository.findByUserId(userId).map { roomMapper.toRoomOutDTO(it) }
  }
}