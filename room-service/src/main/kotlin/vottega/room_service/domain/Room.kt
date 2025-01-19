package vottega.room_service.domain

import jakarta.persistence.*
import org.hibernate.annotations.Filter
import org.springframework.dao.DuplicateKeyException
import vottega.room_service.domain.enumeration.RoomStatus
import vottega.room_service.dto.ParticipantInfoDTO
import vottega.room_service.exception.ParticipantNotFoundException
import vottega.room_service.exception.RoleNotFoundException
import vottega.room_service.exception.RoleStatusConflictException
import vottega.room_service.exception.RoomStatusConflictException
import java.time.LocalDateTime
import java.util.*

@Entity
class Room(
  roomName: String,
  ownerId: Long,
) {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long? = null
  var roomName: String = roomName
    private set
  var ownerId: Long = ownerId
    private set

  @OneToMany(mappedBy = "room", orphanRemoval = true, cascade = [CascadeType.ALL])
  var participantList: MutableList<Participant> = mutableListOf()
    private set

  @OneToMany(mappedBy = "room", orphanRemoval = true, cascade = [CascadeType.ALL])
  @Filter(name = "deletedFilter", condition = "deleted_at IS NULL")
  var participantRoleList: MutableList<ParticipantRole> = mutableListOf()
    private set
  var status: RoomStatus = RoomStatus.NOT_STARTED
    private set
  var createdAt: LocalDateTime? = null
    private set
  var lastUpdatedAt: LocalDateTime? = null
    private set
  var startedAt: LocalDateTime? = null
    private set
  var finishedAt: LocalDateTime? = null
    private set

  fun addParticipant(participantInfoDTO: ParticipantInfoDTO): Participant {
    val participantRole = participantRoleList.find { it.role == participantInfoDTO.role }
      ?: throw RoleNotFoundException(participantInfoDTO.role)
    if (participantList.find { it.name == participantInfoDTO.name } != null) {
      throw DuplicateKeyException("Participant name is duplicated")
    }
    val participant = Participant(
      participantInfoDTO.name,
      participantInfoDTO.phoneNumber,
      participantInfoDTO.position,
      participantRole,
      false,
      this,
    )
    this.participantList.add(participant)
    return participant
  }

  fun addParticipantRole(role: String, canVote: Boolean) {
    if (this.participantRoleList.find { it.role == role } != null) {
      throw RoleStatusConflictException(role)
    }
    this.participantRoleList.add(ParticipantRole(this, role, canVote))
  }

  fun deleteParticipantRole(role: String) {
    val participantRole = this.participantRoleList.find { it.role == role }
    if (participantRole == null) {
      throw RoleNotFoundException(role)
    }
    this.participantRoleList.remove(participantRole)
  }

  fun updateParticipantRole(role: String, canVote: Boolean) {
    val participantRole = this.participantRoleList.find { it.role == role }
    if (participantRole == null) {
      throw RoleNotFoundException(role)
    }
    participantRole.updateCanVote(canVote)
  }


  fun removeParticipant(uuid: UUID) {
    val participant = this.participantList.find { it.id == uuid }
    if (participant == null) {
      throw ParticipantNotFoundException(uuid)
    }
    participant.remove()
  }

  fun updateParticipant(uuid: UUID, participantInfoDTO: ParticipantInfoDTO): Participant {
    val participant = this.participantList.find { it.id == uuid }
    if (participant == null) {
      throw ParticipantNotFoundException(uuid)
    }
    val participantRole = (this.participantRoleList.find { it.role == participantInfoDTO.role }
      ?: throw RoleNotFoundException(participantInfoDTO.role))

    return participant.updateParticipant(
      participantInfoDTO.name,
      participantInfoDTO.phoneNumber,
      participantInfoDTO.position,
      participantRole
    )
  }

  fun enterParticipant(uuid: UUID) {
    val participant = this.participantList.find { it.id == uuid }
    if (participant == null) {
      throw ParticipantNotFoundException(uuid)
    }
    participant.enter()
  }

  fun exitParticipant(uuid: UUID) {
    val participant = this.participantList.find { it.id == uuid }
    if (participant == null) {
      throw ParticipantNotFoundException(uuid)
    }
    participant.exit()
  }

  fun update(roomName: String? = null, status: RoomStatus? = null) {
    roomName?.let { this.roomName = it }
    status?.let {
      when (it) {
        RoomStatus.PROGRESS -> start()
        RoomStatus.FINISHED -> finish()
        RoomStatus.STOPPED -> stop()
        else -> throw RoomStatusConflictException(it)
      }
    }
  }

  private fun start() {
    if (this.status != RoomStatus.NOT_STARTED && this.status != RoomStatus.STOPPED) {
      throw RoomStatusConflictException(this.status)
    }
    this.status = RoomStatus.PROGRESS
    this.startedAt = LocalDateTime.now()
    this.lastUpdatedAt = LocalDateTime.now()
  }

  private fun finish() {
    if (this.status != RoomStatus.PROGRESS) {
      throw RoomStatusConflictException(this.status)
    }
    this.status = RoomStatus.FINISHED
    this.finishedAt = LocalDateTime.now()
    this.lastUpdatedAt = LocalDateTime.now()
  }

  private fun stop() {
    if (this.status != RoomStatus.PROGRESS) {
      throw RoomStatusConflictException(this.status)
    }
    this.status = RoomStatus.STOPPED
    this.lastUpdatedAt = LocalDateTime.now()
  }

  @PrePersist
  fun prePersist() {
    this.createdAt = LocalDateTime.now()
    this.lastUpdatedAt = LocalDateTime.now()
  }

  @PreUpdate()
  fun preUpdate() {
    this.lastUpdatedAt = LocalDateTime.now()
  }
}