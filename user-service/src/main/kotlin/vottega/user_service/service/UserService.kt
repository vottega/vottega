package vottega.user_service.service

import jakarta.transaction.Transactional
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import vottega.user_service.client.AuthClient
import vottega.user_service.domain.User
import vottega.user_service.dto.AuthResponseDTO
import vottega.user_service.dto.UserAuthRequestDTO
import vottega.user_service.dto.UserResponse
import vottega.user_service.dto.mapper.UserMapper
import vottega.user_service.repository.UserRepository

@Service
@Transactional
class UserService(
  private val emailAuthService: EmailAuthService,
  private val userRepository: UserRepository,
  private val passwordEncoder: PasswordEncoder,
  private val userMapper: UserMapper,
  private val authClient: AuthClient
) {
  fun createUser(
    name: String,
    userId: String,
    email: String,
    password: String,
    emailAuthCode: String
  ): UserResponse {
    if (!emailAuthService.verifyEmail(email, emailAuthCode)) {
      throw BadCredentialsException("Email verification failed")
    }
    val user = User(username = name, userId = userId, email = email, password = passwordEncoder.encode(password))
    userRepository.save(user)
    return userMapper.toUserDTO(user)
  }

  fun checkUserIdDuplication(userId: String): Boolean {
    return userRepository.existsByUserId(userId)
  }

  fun checkEmailDuplication(email: String): Boolean {
    return userRepository.existsByEmail(email)
  }

  fun validateUser(userId: String, password: String): AuthResponseDTO {
    val user = userRepository.findByUserId(userId)
    if (user != null && passwordEncoder.matches(password, user.password)) {
      return authClient.getRoom(UserAuthRequestDTO(id = user.id!!, userId = userId))
    }
    throw BadCredentialsException("Invalid userId or password")
  }

  //security 추가
  fun updateUser(userId: String, name: String?, password: String?): UserResponse {
    if (name != null) {
      val existUser = userRepository.findByUsername(name)
      if (existUser != null) {
        throw IllegalArgumentException("이미 존재하는 이름입니다.")
      }
      val user = userRepository.findByUserId(userId)
      user?.updateName(name)
    }
    if (password != null) {
      val user = userRepository.findByUserId(userId)
      user?.updatePassword(passwordEncoder.encode(password))
    }
    val user = userRepository.findByUserId(userId)
    return userMapper.toUserDTO(user ?: throw IllegalArgumentException("존재하지 않는 유저입니다."))
  }

  //security 추가
  fun deleteUser(userId: String) {
    userRepository.deleteByUserId(userId)
  }

  fun getList(): List<UserResponse> {
    val userList = userRepository.findAll()
    return userList.map {
      userMapper.toUserDTO(it)
    }
  }
}