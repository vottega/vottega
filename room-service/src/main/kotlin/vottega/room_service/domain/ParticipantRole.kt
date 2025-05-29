package vottega.room_service.domain

import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@SQLDelete(sql = "UPDATE participant_role SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
class ParticipantRole(
  @ManyToOne @JoinColumn(name = "room_id", nullable = false) var room: Room,
  role: String,
  canVote: Boolean = true,
) {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long? = null

  var role: String = role
    private set
  var canVote: Boolean = canVote
    private set

  var createdAt: LocalDateTime? = null
    private set

  var lastUpdatedAt: LocalDateTime? = null
    private set

  var deletedAt: LocalDateTime? = null
    private set


  fun updateCanVote(canVote: Boolean) {
    this.canVote = canVote
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