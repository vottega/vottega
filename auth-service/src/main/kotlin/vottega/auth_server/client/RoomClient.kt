package vottega.auth_server.client

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.*

@Service
class RoomClient(private val webClientBuilder: WebClient.Builder) {
  fun getUserById(userId: UUID): Mono<UserResponse> {
    return webClientBuilder.build()
      .get()
      .uri("http://room-service/api/room/participants/${userId}")
      .retrieve()
      .bodyToMono(UserResponse::class.java)
  }
}

data class UserResponse(
  val roomId: Long,
)