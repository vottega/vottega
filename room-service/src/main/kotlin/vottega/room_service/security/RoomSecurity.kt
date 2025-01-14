package vottega.room_service.security

import org.springframework.stereotype.Component
import vottega.room_service.exception.RoomNotFoundException
import vottega.room_service.repository.RoomRepository
import java.util.*

@Component
class RoomSecurity(private val roomRepository: RoomRepository) {
  fun isParticipantInRoomAndCanVote(roomId: Long, participantId: UUID, canVote: Boolean?): Boolean {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    val participant = room.participantList.find { it.id == participantId } ?: return false
    return canVote?.let { participant.participantRole.canVote == it } ?: true
  }

  fun isOwner(roomId: Long, ownerId: Long): Boolean {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    return room.ownerId == ownerId
  }
}