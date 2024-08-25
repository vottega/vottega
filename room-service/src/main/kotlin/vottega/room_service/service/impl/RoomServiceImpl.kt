package vottega.room_service.service.impl

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import vottega.room_service.domain.Room
import vottega.room_service.domain.enumeration.RoomStatus
import vottega.room_service.dto.ParticipantInfoDTO
import vottega.room_service.dto.ParticipantRoleDTO
import vottega.room_service.dto.RoomResponseDTO
import vottega.room_service.dto.mapper.RoomMapper
import vottega.room_service.repository.RoomRepository
import vottega.room_service.service.RoomService
import java.util.*

@Service
@Transactional
class RoomServiceImpl(
    private val roomRepository: RoomRepository,
    private val roomMapper: RoomMapper,
) : RoomService {

    override fun createRoom(
        roomName: String,
        ownerId: Long,
        participantInfoDTOS: List<ParticipantInfoDTO>
    ): RoomResponseDTO {
        val room = Room(roomName, ownerId)
        roomRepository.save(room)
        participantInfoDTOS.forEach {
            room.addParticipant(it)
        }
        return roomMapper.toRoomOutDTO(room)
    }

    override fun updateRoom(roomId: Long, roomName: String?, status: RoomStatus?): RoomResponseDTO {
        val room = roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
        room.apply {
            roomName?.let { updateRoomName(it) }
            status?.let {
                when (it) {
                    RoomStatus.PROGRESS -> start()
                    RoomStatus.FINISHED -> finish()
                    RoomStatus.STOPPED -> stop()
                    else -> throw IllegalArgumentException("Invalid status")
                }
            }
        }
        return roomMapper.toRoomOutDTO(room)
    }

    override fun addParticipant(roomId: Long, participantInfoDTOS: List<ParticipantInfoDTO>): RoomResponseDTO {
        val room = roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
        participantInfoDTOS.forEach {
            room.addParticipant(it)
        }
        return roomMapper.toRoomOutDTO(room)
    }

    override fun removeParticipant(roomId: Long, participantId: UUID): RoomResponseDTO {
        val room = roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
        room.apply { removeParticipant(participantId) }
        return roomMapper.toRoomOutDTO(room)
    }

    override fun updateParticipant(
        roomId: Long,
        participantId: UUID,
        participantInfoDTO: ParticipantInfoDTO
    ): RoomResponseDTO {
        val room = roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
        room.apply { updateParticipant(participantId, participantInfoDTO) }
        return roomMapper.toRoomOutDTO(room)
    }


    override fun enterParticipant(roomId: Long, participantId: UUID): RoomResponseDTO {
        val room = roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
        room.apply { enterParticipant(participantId) }
        return roomMapper.toRoomOutDTO(room)
    }

    override fun exitParticipant(roomId: Long, participantId: UUID): RoomResponseDTO {
        val room = roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
        room.apply { exitParticipant(participantId) }
        return roomMapper.toRoomOutDTO(room)
    }

    override fun addRole(roomId: Long, roleInfo: ParticipantRoleDTO): RoomResponseDTO {
        val room = roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
        room.apply { addParticipantRole(roleInfo.role, roleInfo.canVote) }
        return roomMapper.toRoomOutDTO(room)
    }

    override fun getRoom(roomId: Long): RoomResponseDTO {
        val room = roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
        return roomMapper.toRoomOutDTO(room)
    }

    override fun getRoomList(userId: Long): List<RoomResponseDTO> {
        return roomRepository.findByUserId(userId).map { roomMapper.toRoomOutDTO(it) }
    }
}