package vottega.user_service.repository

import org.springframework.data.jpa.repository.JpaRepository
import vottega.user_service.domain.User


interface UserRepository : JpaRepository<User, Long> {
  fun findByUserId(userId: String): User?
  fun findByUsername(username: String): User?
  fun existsByUserId(userId: String): Boolean
  fun existsByEmail(email: String): Boolean
  fun deleteByUserId(userId: String)
}