package vottega.room_service.domain

import jakarta.persistence.*

@Entity
class ParticipantRole(
    room: Room,
    role: String,
    canVote: Boolean = true
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    var room: Room = room
        private set
    var role: String = role
        private set
    var canVote: Boolean = canVote
        private set

    fun updateCanVote(canVote: Boolean) {
        this.canVote = canVote
    }
}