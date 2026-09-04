package vottega.auth_server.client

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class UserClient(private val webClientBuilder: WebClient.Builder) {
  fun authenticateUser(userId: String, password: String): Mono<AuthResponseDTO> {
    return webClientBuilder.baseUrl("lb://USER-SERVICE").build()
      .post()
      .uri("/api/user/login")
      .bodyValue(LoginRequest(userId, password))
      .retrieve()
      .bodyToMono(AuthResponseDTO::class.java)
  }
}

data class AuthResponseDTO(
  val verified: Boolean,
  val id: Long?,
)

data class LoginRequest(
  val userId: String,
  val password: String
)