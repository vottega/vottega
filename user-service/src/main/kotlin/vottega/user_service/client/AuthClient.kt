package vottega.user_service.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import vottega.user_service.dto.AuthResponseDTO
import vottega.user_service.dto.UserAuthRequestDTO

@FeignClient(name = "auto-service")
interface AuthClient {
  @PostMapping("api/auth")
  fun getRoom(userAuthRequestDTO: UserAuthRequestDTO): AuthResponseDTO
}