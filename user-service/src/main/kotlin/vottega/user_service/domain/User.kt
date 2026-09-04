package vottega.user_service.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
  uniqueConstraints = [
    UniqueConstraint(columnNames = ["userId"]),
    UniqueConstraint(columnNames = ["email"])
  ],
  indexes = [
    Index(name = "idx_user_userId", columnList = "userId"),
    Index(name = "idx_user_email", columnList = "email")
  ]
)
data class User(
  @Column(nullable = false, length = 15)
  var username: String,

  @Column(nullable = false, length = 15)
  var userId: String,

  @Column(nullable = false)
  var password: String,
  @Column
  var email: String,
) {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long? = null

  @Column
  var createdAt: LocalDateTime? = null

  @Column
  var lastUpdatedAt: LocalDateTime? = null

  fun updateName(name: String) {
    this.username = name
  }

  fun updatePassword(password: String) {
    this.password = password
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