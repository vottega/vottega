package vottega.gateway.filter.global

import feign.FeignException
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import vottega.gateway.client.AuthReactiveFeignClient
import vottega.gateway.config.ServiceUrlProperties
import vottega.gateway.dto.AuthResponse
import vottega.gateway.dto.Role
import java.net.URI

@Component
class JwtParsingFilter(
  @Lazy private val authReactiveFeignClient: AuthReactiveFeignClient,
  private val serviceUrlProperties: ServiceUrlProperties
) : GlobalFilter {
  override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
    val path = exchange.request.uri.path

    // 1) /user/login 은 인증 없이 통과
    if (path.startsWith("/user/login")) {
      // 라우팅 주소만 변경
      val newUri = serviceUrlProperties.userServiceUrl  // user-service
      mutateRequestURI(exchange, URI(newUri))
      return chain.filter(exchange)
    }

    if (path.startsWith("/user") ||
      path.startsWith("/room") ||
      path.startsWith("/vote")
    ){
      val token = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
      if (token.isNullOrBlank()) {
        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        return exchange.response.setComplete()
      }

      return authReactiveFeignClient.validateToken(token)
        .flatMap { authResponse ->
          // 토큰 검증 OK -> 실제 서비스 라우팅
          val newUri = when {
            path.startsWith("/room") -> URI("${serviceUrlProperties.roomServiceUrl}$path")
            path.startsWith("/user") -> URI("${serviceUrlProperties.userServiceUrl}$path")
            path.startsWith("/vote") -> URI("${serviceUrlProperties.voteServiceUrl}$path")
            else -> null
          }
          if (newUri == null) {
            exchange.response.statusCode = HttpStatus.NOT_FOUND
            return@flatMap exchange.response.setComplete()
          }


          val mutatedExchange = mutateHeadersByRole(exchange, authResponse)
          mutateRequestURI(mutatedExchange, newUri)
          chain.filter(mutatedExchange)
        }
        .onErrorResume(FeignException::class.java) { ex ->
          when (ex.status()) {
            401 -> {
              exchange.response.statusCode = HttpStatus.UNAUTHORIZED
              exchange.response.setComplete()
            }
            403 -> {
              exchange.response.statusCode = HttpStatus.FORBIDDEN
              exchange.response.setComplete()
            }
            else -> {
              exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
              exchange.response.setComplete()
            }
          }
        }
    }
    exchange.response.statusCode = HttpStatus.NOT_FOUND
    return exchange.response.setComplete()
  }

  private fun mutateHeadersByRole(exchange: ServerWebExchange, auth: AuthResponse): ServerWebExchange {
    val originalRequest = exchange.request.mutate()

    when (auth.role) {
      Role.USER -> {
        auth.userId?.let { userIdValue ->
          originalRequest.header("X-User-Id", userIdValue.toString())
        }
        originalRequest.header("X-Role", auth.role.toString())
      }
      Role.PARTICIPANT -> {
        auth.participantId?.let { participantIdValue ->
          originalRequest.header("X-Participant-Id", participantIdValue.toString())
        }
        auth.roomId?.let { roomIdValue ->
          originalRequest.header("X-Room-Id", roomIdValue.toString())
        }
        originalRequest.header("X-Role", auth.role.toString())
      }
    }

    // 새로 만든 Request로 교체
    val newRequest = originalRequest.build()
    return exchange.mutate().request(newRequest).build()
  }

  private fun mutateRequestURI(exchange: ServerWebExchange, newUri: URI) {
    exchange.attributes[ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR] = newUri
  }


}