package vottega.room_service.security

import org.springframework.stereotype.Component
import vottega.room_service.exception.RoomNotFoundException
import vottega.room_service.repository.RoomRepository
import java.util.*

@Component
class RoomSecurity(private val roomRepository: RoomRepository) {
  fun isParticipantInRoom(roomId: Long, participantId: UUID): Boolean {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    return room.participantList.any { it.id == participantId }
  }

  fun isParticipantInRoom(roomId: Long, ownerId: Long): Boolean {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    return room.ownerId == ownerId
  }

  fun isOwner(roomId: Long, ownerId: Long): Boolean {
    val room = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException(roomId) }
    return room.ownerId == ownerId
  }
}