package vottega.room_service.repository

import org.springframework.data.jpa.repository.JpaRepository
import vottega.room_service.domain.Room

interface RoomRepository : JpaRepository<Room, Long> {
}