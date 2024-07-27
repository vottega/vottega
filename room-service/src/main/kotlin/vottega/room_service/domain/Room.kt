package vottega.room_service.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import vottega.room_service.domain.enumeration.RoomStatus
import vottega.room_service.domain.vo.Qualification
import java.time.LocalDateTime
import java.util.UUID

@Entity
class Room(
    roomName : String,
    owserId : Long,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null
    var roomName : String = roomName
        private set
    var owserId : Long = owserId
        private set
    @OneToMany(mappedBy = "room", orphanRemoval = true)
    var participantList: MutableList<Participant> = mutableListOf()
        private set
    var status : RoomStatus = RoomStatus.NOT_STARTED
        private set
    var createdAt : LocalDateTime? = null
        private set
    var lastModifiedAt : LocalDateTime? = null
        private set
    var startedAt : LocalDateTime? = null
        private set
    var finishedAt : LocalDateTime? = null
        private set

    fun updateRoomName(roomName: String){
        this.roomName = roomName
        this.lastModifiedAt = LocalDateTime.now()
    }

    fun addParticipant(name : String, qualification: Qualification){
        this.participantList.add(Participant(name, qualification, false, this))
    }

    fun removeParticipant(uuid: UUID){
        val participant = this.participantList.find { it.id == uuid }
        if(participant == null){
            throw IllegalArgumentException("Participant not found")
        }
        this.participantList.remove(participant)
    }

    fun updateParticipant(uuid: UUID, name: String? = null, qualification: Qualification? = null){
        val participant = this.participantList.find { it.id == uuid }
        if(participant == null){
            throw IllegalArgumentException("Participant not found")
        }
        participant.updateParticipant(name, qualification)
    }

    fun enterParticipant(uuid: UUID){
        val participant = this.participantList.find { it.id == uuid }
        if(participant == null){
            throw IllegalArgumentException("Participant not found")
        }
        participant.enter()
    }

    fun exitParticipant(uuid: UUID){
        val participant = this.participantList.find { it.id == uuid }
        if(participant == null){
            throw IllegalArgumentException("Participant not found")
        }
        participant.exit()
    }

    fun start(){
        if(this.status != RoomStatus.NOT_STARTED || this.status != RoomStatus.STOPPED){
            throw IllegalStateException("Room is already started")
        }
        this.status = RoomStatus.PROGRESS
        this.startedAt = LocalDateTime.now()
        this.lastModifiedAt = LocalDateTime.now()
    }

    fun finish(){
        if (this.status != RoomStatus.PROGRESS){
            throw IllegalStateException("Room is not in progress")
        }
        this.status = RoomStatus.FINISHED
        this.finishedAt = LocalDateTime.now()
        this.lastModifiedAt = LocalDateTime.now()
    }

    fun stop(){
        if (this.status == RoomStatus.STOPPED){
            throw IllegalStateException("Room is not in progress")
        }
        this.status = RoomStatus.STOPPED
        this.lastModifiedAt = LocalDateTime.now()
    }

    @PrePersist
    fun prePersist(){
        this.createdAt = LocalDateTime.now()
        this.lastModifiedAt = LocalDateTime.now()
    }

    @PreUpdate()
    fun preUpdate(){
        this.lastModifiedAt = LocalDateTime.now()
    }
}