package vottega.user_service.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import vottega.user_service.dto.AuthResponseDTO
import vottega.user_service.dto.UserAuthRequestDTO

@FeignClient(name = "auth-service")
interface AuthClient {
  @PostMapping("api/auth/user")
  fun getRoom(userAuthRequestDTO: UserAuthRequestDTO): AuthResponseDTO
}