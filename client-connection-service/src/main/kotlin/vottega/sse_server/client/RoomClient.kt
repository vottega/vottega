package vottega.sse_server.client

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

@Component
class RoomClient(private val webClientBuilder: WebClient.Builder) {
  fun getUserRoomList(): Flux<RoomResponseDTO> =
    webClientBuilder.build()
      .get()
      .uri("/api/room/list")
      .retrieve()
      .bodyToFlux(RoomResponseDTO::class.java)
}

data class RoomResponseDTO(
  val id: Long,
  val name: String,
  val ownerId: Long,
)