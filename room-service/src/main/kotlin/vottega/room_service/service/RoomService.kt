package vottega.room_service.service.impl

import jakarta.transaction.Transactional
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import vottega.room_service.adaptor.RoomProducer
import vottega.room_service.avro.Action
import vottega.room_service.domain.Room
import vottega.room_service.domain.enumeration.RoomStatus
import vottega.room_service.dto.ParticipantInfoDTO
import vottega.room_service.dto.ParticipantRoleDTO
import vottega.room_service.dto.ParticipantRoomDTO
import vottega.room_service.dto.RoomResponseDTO
import vottega.room_service.dto.mapper.ParticipantMapper
import vottega.room_service.dto.mapper.RoomMapper
import vottega.room_service.exception.ParticipantNotFoundException
import vottega.room_service.exception.RoomNotFoundException
import vottega.room_service.repository.ParticipantRepository
import vottega.room_service.repository.RoomRepository
import java.util.*

@Service
@Transactional
class RoomService(
  private val roomRepository: RoomRepository,
  private val participantRepository: ParticipantRepository,
  private val roomMapper: RoomMapper,
  private val roomProducer: RoomProducer,
  private val participantMapper: ParticipantMapper
) {

  @PreAuthorize("hasRole('USER')") //  todo ownerid를 받지 말고 token parsing해서 받도록
  fun createRoom(
    roomName: String,
    ownerId: Long,
    participantRoleDTOList: List<ParticipantRoleDTO>,
  ): RoomResponseDTO {
    val room = Room(roomName, ownerId)
    roomRepository.save(room)
    participantRoleDTOList.forEach { room.addParticipantRole(it.role, it.canVote) }

    return roomMapper.toRoomOutDTO(room)
  }


  @PreAuthorize("@roomSecurity.isOwner(#roomId, authentication.principal)")
  fun updateRoom(roomId: Long, roomName: String?, status: RoomStatus?): RoomResponseDTO {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    room.update(roomName, status)
    val roomDTO = roomMapper.toRoomOutDTO(room)
    roomProducer.roomEditMessageProduce(roomDTO)
    return roomDTO
  }


  @PreAuthorize("@roomSecurity.isOwner(#roomId, authentication.principal)")
  fun addParticipant(roomId: Long, participantInfoDTOS: List<ParticipantInfoDTO>): RoomResponseDTO {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    participantInfoDTOS.forEach {
      room.addParticipant(it)
    }
    roomRepository.save(room)
    participantInfoDTOS.forEach { participantInfoDTO ->
      room.participantList.find { participantInfoDTO.name == it.name }?.let { participant ->
        roomProducer.participantEditMessageProduce(participantMapper.toParticipantResponseDTO(participant), Action.ADD)
      }
    }
    return roomMapper.toRoomOutDTO(room)
  }


  @PreAuthorize("@roomSecurity.isOwner(#roomId, authentication.principal)")
  fun removeParticipant(roomId: Long, participantId: UUID): RoomResponseDTO {
    //TODO 방장은 못지우게
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    val removedParticipant = room.removeParticipant(participantId)
    roomRepository.save(room)
    roomProducer.participantEditMessageProduce(
      participantMapper.toParticipantResponseDTO(removedParticipant),
      Action.DELETE
    )
    return roomMapper.toRoomOutDTO(room)
  }


  @PreAuthorize("@roomSecurity.isOwner(#roomId, authentication.principal)")
  fun updateParticipant(
    roomId: Long,
    participantId: UUID,
    participantInfoDTO: ParticipantInfoDTO
  ): RoomResponseDTO {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    val updatedParticipant = room.updateParticipant(participantId, participantInfoDTO)
    val participantDTO = participantMapper.toParticipantResponseDTO(updatedParticipant)
    roomProducer.participantEditMessageProduce(participantDTO, Action.EDIT)
    return roomMapper.toRoomOutDTO(room)
  }


  fun enterParticipant(roomId: Long, participantId: UUID): RoomResponseDTO {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    room.apply { enterParticipant(participantId) }
    return roomMapper.toRoomOutDTO(room)
  }

  fun exitParticipant(roomId: Long, participantId: UUID): RoomResponseDTO {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    room.apply { exitParticipant(participantId) }
    return roomMapper.toRoomOutDTO(room)
  }


  @PreAuthorize("@roomSecurity.isOwner(#roomId, authentication.principal)")
  fun addRole(roomId: Long, roleInfo: ParticipantRoleDTO): RoomResponseDTO {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    room.apply { addParticipantRole(roleInfo.role, roleInfo.canVote) }
    return roomMapper.toRoomOutDTO(room)
  }


  @PreAuthorize("@roomSecurity.isOwner(#roomId, authentication.principal)")
  fun deleteRole(roomId: Long, role: String): RoomResponseDTO {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    room.apply { deleteParticipantRole(role) }
    return roomMapper.toRoomOutDTO(room)
  }

  @PreAuthorize("@roomSecurity.isParticipantInRoom(#roomId, authentication.principal)")
  fun getRoom(roomId: Long): RoomResponseDTO {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    return roomMapper.toRoomOutDTO(room)
  }

  fun getRoomList(userId: Long): List<RoomResponseDTO> {
    return roomRepository.findByUserId(userId).map { roomMapper.toRoomOutDTO(it) }
  }

  fun getParticipantRoom(participantId: UUID): ParticipantRoomDTO {
    return participantRepository.findById(participantId).orElseThrow { ParticipantNotFoundException(participantId) }
      .let {
        ParticipantRoomDTO(it.room.id ?: throw IllegalStateException("Room Id is null"))
      }
  }
}