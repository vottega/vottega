package vottega.gateway.client

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import vottega.gateway.dto.Role
import java.util.*

@Service
class AuthWebClient(private val webClientBuilder: WebClient.Builder) {
  fun verifyTokenRaw(token: String): Mono<VerifyResponseDTO> =
    webClientBuilder.baseUrl("lb://AUTH-SERVICE").build()
      .post()
      .uri("/api/auth/verify")
      .retrieve()
      .bodyToMono(VerifyResponseDTO::class.java)
}

data class VerifyResponseDTO(
  val role: Role,
  val participantId: UUID?,
  val roomId: Long?,
  val userId: Long?
)