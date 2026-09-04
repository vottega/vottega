package vottega.room_service.domain

import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
  name = "participant",
  uniqueConstraints = [UniqueConstraint(columnNames = ["name", "room_id"])]
)
@SQLDelete(sql = "UPDATE participant SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
class Participant(
  var name: String,
  var phoneNumber: String? = null,
  var position: String? = null,
  @ManyToOne
  @JoinColumn(name = "qualification_id", nullable = false)
  var participantRole: ParticipantRole,
  var isEntered: Boolean = false,
  @ManyToOne
  @JoinColumn(name = "room_id", nullable = false)
  val room: Room,
) {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  val id: UUID? = null
  var enteredAt: LocalDateTime? = null
  var createdAt: LocalDateTime? = null
  var lastUpdatedAt: LocalDateTime? = null
  var deletedAt: LocalDateTime? = null


  fun enter() {
    this.isEntered = true
    this.enteredAt = LocalDateTime.now()
  }

  fun exit() {
    this.isEntered = false
    this.enteredAt = LocalDateTime.now()
  }

  fun remove() {
    this.deletedAt = LocalDateTime.now()
  }

  fun updateParticipant(
    name: String? = null,
    phoneNumber: String?,
    position: String? = null,
    participantRole: ParticipantRole? = null
  ): Participant {
    name?.let { this.name = it }
    phoneNumber?.let { this.phoneNumber = it }
    position?.let { this.position = it }
    participantRole?.let { this.participantRole = it }
    return this
  }

  @PrePersist
  fun prePersist() {
    this.createdAt = LocalDateTime.now()
    this.lastUpdatedAt = LocalDateTime.now()
  }

  @PreUpdate
  fun preUpdate() {
    this.lastUpdatedAt = LocalDateTime.now()
  }
}