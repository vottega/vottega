package vottega.gateway.client

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono
import vottega.gateway.dto.AuthResponse

@ReactiveFeignClient(name = "auth-service")
interface AuthReactiveFeignClient {

  @PostMapping("/api/auth/validate")
  fun validateToken(@RequestParam token: String): Mono<AuthResponse>
}