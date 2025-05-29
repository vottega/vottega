package vottega.room_service.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import vottega.room_service.domain.ParticipantRole

@Repository
interface ParticipantRoleRepository : JpaRepository<ParticipantRole, Long> {
  @Modifying
  fun deleteByRoomId(roomId: Long)
}