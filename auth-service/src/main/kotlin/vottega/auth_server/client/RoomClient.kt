package vottega.auth_server.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import reactor.core.publisher.Mono
import java.util.*


@FeignClient(name = "room-service", url = "http://localhost:8082")
interface RoomClient {
    @GetMapping("/users/{id}")
    fun getUserById(@PathVariable("id") id: UUID): Mono<UserResponse>
}

data class UserResponse(
    val id: UUID,
    val roomId : Long,
)