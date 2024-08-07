package vottega.room_service.service.impl

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import vottega.room_service.domain.Room
import vottega.room_service.domain.enumeration.RoomStatus
import vottega.room_service.domain.vo.Qualification
import vottega.room_service.dto.ParticipantInfoDTO
import vottega.room_service.repository.RoomRepository
import vottega.room_service.service.RoomService
import java.util.*

@Service
@Transactional
class RoomServiceImpl(
    private val roomRepository: RoomRepository,
) : RoomService {


    @Transactional
    override fun createRoom(
        roomName: String,
        ownerId: Long,
        ParticipantInfoDTOS: List<ParticipantInfoDTO>
    ): Room {
        val room = Room(roomName, ownerId)
        ParticipantInfoDTOS.forEach {
            var qualification = Qualification(it.position, it.role, it.canVote)
            room.addParticipant(it.name, qualification)
        }
        roomRepository.save(room)
        return room;
    }

    override fun updateRoom(roomId: Long, roomName: String?, status: RoomStatus?): Room {
        val room = roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
        return room.apply {
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
    }

    override fun addParticipant(roomId: Long, ParticipantInfoDTOS: List<ParticipantInfoDTO>): Room {
        val room = roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
        ParticipantInfoDTOS.forEach {
            val qualification = Qualification(it.position, it.role, it.canVote)
            room.addParticipant(it.name, qualification)
        }
        return room
    }

    override fun removeParticipant(roomId: Long, participantId: UUID): Room {
        val room = roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
        return room.apply { removeParticipant(participantId) }
    }

    override fun updateParticipant(roomId: Long, participantId: UUID, participantInfoDTO: ParticipantInfoDTO): Room {
        val room = roomRepository.findById(roomId).orElseThrow { IllegalArgumentException("Room not found") }
        val qualification =
            Qualification(participantInfoDTO.position, participantInfoDTO.role, participantInfoDTO.canVote)
        return room.apply { updateParticipant(participantId, participantInfoDTO.name, qualification) }
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