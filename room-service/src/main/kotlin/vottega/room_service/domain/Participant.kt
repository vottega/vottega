package vottega.room_service.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import vottega.room_service.domain.enumeration.ParticipantRole
import java.time.LocalDateTime
import java.util.*

@Entity
data class Participant (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id : UUID,
    var name : String,
    var position : String,
    var role : ParticipantRole,
    var canVote : Boolean,
    var isEntered : Boolean,
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    val room : Room,
    var enteredAt : LocalDateTime,
    var createdAt : LocalDateTime,
    var lastUpdatedAt : LocalDateTime,
    ){
}