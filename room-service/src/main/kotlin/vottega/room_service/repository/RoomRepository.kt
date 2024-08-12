package vottega.room_service.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import vottega.room_service.domain.Room

interface RoomRepository : JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r join fetch r.participantList WHERE r.ownerId = :userId")
    fun findByUserId(userId: Long): List<Room>
}