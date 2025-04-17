package vottega.gateway.client

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class AuthWebClient(private val webClientBuilder: WebClient.Builder) {
  fun verifyTokenRaw(token: String): Mono<ClientResponse> =
    webClientBuilder.baseUrl("lb://AUTH-SERVICE").build()
      .post()
      .uri("/api/auth/verify")
      .bodyValue(token)
      .exchangeToMono { Mono.just(it) }
}