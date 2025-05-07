package vottega.sse_server.client

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

@Component
class RoomClient(private val webclient: WebClient) {
  fun getUserRoomList(userId: Long): Flux<RoomResponseDTO> =
    webclient
      .post()
      .uri("/api/room/list/{userId}")
      .retrieve()
      .bodyToFlux(RoomResponseDTO::class.java)
}

data class RoomResponseDTO(
  val id: Long,
  val name: String,
  val description: String,
  val ownerId: Long,
  val createdAt: String,
  val updatedAt: String
)