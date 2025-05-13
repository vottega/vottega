package vottega.user_service.web

import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import vottega.user_service.dto.UserResponse
import vottega.user_service.service.UserService

@Profile("local")
@RestController
class LocalController(private val userService: UserService) {
  @PostMapping("/api/user/list")
  fun getList(): List<UserResponse> {
    return userService.getList()
  }
}