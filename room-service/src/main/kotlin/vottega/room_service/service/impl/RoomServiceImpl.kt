package vottega.room_service.service.impl

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import vottega.room_service.domain.Room
import vottega.room_service.domain.enumeration.ParticipantRole
import vottega.room_service.domain.enumeration.RoomStatus
import vottega.room_service.domain.vo.Qualification
import vottega.room_service.dto.participantInfoDTO
import vottega.room_service.repository.RoomRepository
import vottega.room_service.service.RoomService
import java.util.*

@Service
@Transactional
class RoomServiceImpl(
    private val roomRepository: RoomRepository,
) : RoomService  {


    @Transactional
    override fun createRoom(
        roomName: String,
        ownerId: Long,
        participantInfoDTOs: List<participantInfoDTO>
    ): Room {
        val room = Room(roomName, ownerId)
        participantInfoDTOs.forEach {
            var qualification = Qualification(it.position, it.role, it.canVote)
            room.addParticipant(it.name, qualification)
        }
        roomRepository.save(room)
        return room;
    }

    override fun updateRoom(roomId: Long, roomName : String?, status : RoomStatus?): Room {
        val room = roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
        return room.apply {
            roomName?.let { updateRoomName(it) }
            status?.let {
                when(it){
                    RoomStatus.PROGRESS -> start()
                    RoomStatus.FINISHED -> finish()
                    RoomStatus.STOPPED -> stop()
                    else -> throw IllegalArgumentException("Invalid status")
                }
            }
        }
    }

    override fun addParticipant(roomId: Long, name: String, position: String, role: ParticipantRole, canVote: Boolean): Room {
        val room = roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
        val qualification = Qualification(position, role, canVote)
        return room.apply { addParticipant(name, qualification) }
    }

    override fun removeParticipant(roomId: Long, participantId: UUID): Room {
        val room = roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
        return room.apply { removeParticipant(participantId) }
    }

    override fun updateParticipant(
        roomId: Long,
        participantId: UUID,
        name: String,
        position: String,
        role: ParticipantRole,
        canVote: Boolean
    ): Room {
        val room = roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
        val qualification = Qualification(position, role, canVote)
        return room.apply { updateParticipant(participantId, name, qualification) }
    }

    override fun enterParticipant(roomId: Long, participantId: UUID): Room {
        val room = roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
        return room.apply { enterParticipant(participantId) }
    }

    override fun exitParticipant(roomId: Long, participantId: UUID): Room {
        val room = roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
        return room.apply { exitParticipant(participantId) }
    }

    override fun getRoom(roomId: Long): Room {
        return roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
    }
}