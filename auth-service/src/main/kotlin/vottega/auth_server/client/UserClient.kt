package vottega.auth_server.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import reactor.core.publisher.Mono


@FeignClient(name = "user-service", url = "http://localhost:8081")
interface UserClient {
    @PostMapping
    fun authUser(username: String, password: String): Mono<Boolean>

}