package vottega.sse_server.config

import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class WebClientConfig {
  @Bean
  @LoadBalanced
  fun webclient(): WebClient {
    return WebClient.builder()
      .baseUrl("lb://ROOM-SERVICE")
      .defaultHeader("Content-Type", "application/json")
      .filter(ExchangeFilterFunction.ofRequestProcessor { req ->
        Mono.deferContextual { ctxView ->
          val inbound: HttpHeaders = ctxView.get("inboundHeaders") as HttpHeaders
          val newReq = ClientRequest.from(req)
            .headers { it.addAll(inbound) }
            .build()
          Mono.just(newReq)
        }
      })
      .build()
  }
}