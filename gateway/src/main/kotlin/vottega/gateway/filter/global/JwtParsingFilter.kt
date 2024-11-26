package vottega.gateway.filter.global

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtParsingFilter : GlobalFilter {
    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val headers: HttpHeaders = exchange.request.headers
        val jwtToken = headers.getFirst(HttpHeaders.AUTHORIZATION)?.removePrefix("Bearer ")
        if(jwtToken != null) {
            try {
                val claims = parseJwt(jwtToken)
                val mutatedExchange = exchange.mutate()
                    .request { builder ->
                        builder.header("userId", claims["userId"] as String) // 예: userId를 헤더에 추가
                        builder.header("role", claims["role"] as String) // 예: role을 헤더에 추가
                        builder.header("roomId", claims["roomId"] as String) // 예: roomId를 헤더에 추가
                    }
                    .build()

                return chain.filter(mutatedExchange)
            }
            catch (e: Exception) {
                exchange.response.statusCode = org.springframework.http.HttpStatus.UNAUTHORIZED
                return exchange.response.setComplete()
            }
        }
        exchange.response.statusCode = org.springframework.http.HttpStatus.UNAUTHORIZED
        return exchange.response.setComplete()
    }

    private fun parseJwt(jwt: String): Map<String, Any> {
        // JWT 파싱 로직 (여기서는 간단히 Claim을 Mock)
        return mapOf("userId" to "12345", "role" to "USER", "roomId" to 123)
    }
}