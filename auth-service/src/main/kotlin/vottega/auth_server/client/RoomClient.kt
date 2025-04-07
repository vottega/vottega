package vottega.auth_server.client

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono
import java.util.*


@ReactiveFeignClient(name = "room-service")
interface RoomClient {
  @GetMapping("/participants/{userId}")
  fun getUserById(@PathVariable("userId") userId: UUID): Mono<UserResponse>
}

data class UserResponse(
  val roomId: Long,
)