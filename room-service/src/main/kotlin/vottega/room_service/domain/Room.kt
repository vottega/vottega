package vottega.room_service.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import vottega.room_service.domain.enumeration.RoomStatus
import java.time.LocalDateTime

@Entity
class Room(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long,
    var roomName : String,
    var owserId : Long,
    @OneToMany(mappedBy = "room", orphanRemoval = true)
    var participantList: MutableList<Participant>,
    var status : RoomStatus,
    val createdAt : LocalDateTime,
    var lastModifiedAt : LocalDateTime,
    var startedAt : LocalDateTime,
    var finishedAt : LocalDateTime,
) {

}