package vottega.user_service.dto.mapper

import org.springframework.stereotype.Component
import vottega.user_service.domain.User
import vottega.user_service.dto.UserResponse

@Component
class UserMapper {
  fun toUserDTO(user: User): UserResponse {
    return UserResponse(
      id = user.id ?: throw IllegalStateException("유저 아이디가 null 입니다."),
      username = user.username,
      userId = user.userId,
      email = user.email
    )
  }
}