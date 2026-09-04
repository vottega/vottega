package vottega.room_service.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import vottega.room_service.domain.Participant
import java.util.*

interface ParticipantRepository : JpaRepository<Participant, UUID> {
  @Modifying
  fun deleteByRoomId(roomId: Long)
}