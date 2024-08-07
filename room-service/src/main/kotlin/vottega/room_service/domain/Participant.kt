package vottega.room_service.domain

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import vottega.room_service.domain.vo.Qualification
import java.time.LocalDateTime
import java.util.*

@Entity
data class Participant (
    var name : String,
    var position : String,
    @ManyToOne
    @JoinColumn(name = "qualification_id", nullable = false)
    var participantRole: ParticipantRole,
    var isEntered : Boolean,
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    val room : Room,

    ){

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id : UUID? = null
    var enteredAt : LocalDateTime? = null
    var createdAt : LocalDateTime? = null
    var lastUpdatedAt : LocalDateTime? = null
    fun enter(){
        this.isEntered = true
        this.enteredAt = LocalDateTime.now()
    }
    fun exit(){
        this.isEntered = false
        this.enteredAt = LocalDateTime.now()
    }

    fun updateParticipant(name: String? = null, position: String? = null , participantRole: ParticipantRole? = null) {
        name?.let { this.name = it }
        position?.let { this.position = it }
        participantRole?.let { this.participantRole = it }
    }

    @PrePersist
    fun prePersist(){
        this.createdAt = LocalDateTime.now()
        this.lastUpdatedAt = LocalDateTime.now()
    }

    @PreUpdate
    fun preUpdate(){
        this.lastUpdatedAt = LocalDateTime.now()
    }
}