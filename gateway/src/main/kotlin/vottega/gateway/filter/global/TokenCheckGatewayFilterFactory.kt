import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import vottega.gateway.client.AuthWebClient
import vottega.gateway.dto.AuthResponse
import vottega.gateway.dto.Role

@Component("TokenCheck")  // yml 에서 TokenCheck 로 참조
class TokenCheckGatewayFilterFactory(
  private val authWebClient: AuthWebClient
) : AbstractGatewayFilterFactory<TokenCheckGatewayFilterFactory.Config>(Config::class.java) {

  class Config                      // yml 에서 파라미터를 받을 일이 없으면 빈 클래스로 OK

  override fun apply(config: Config): GatewayFilter = GatewayFilter { exchange, chain ->
    val path = exchange.request.uri.path
    if (path.startsWith("/user") || path.startsWith("/auth")) {
      return@GatewayFilter chain.filter(exchange)
    }


    val authHeader = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      exchange.response.statusCode = HttpStatus.UNAUTHORIZED
      return@GatewayFilter exchange.response.setComplete()
    }


    val token = authHeader.removePrefix("Bearer ").trim()

    authWebClient.verifyTokenRaw(token)
      .flatMap { resp ->
        when (resp.statusCode()) {
          HttpStatus.OK -> resp.bodyToMono(AuthResponse::class.java)
            .flatMap { result ->
              val mutated = exchange.request.mutate()
                .header("X-Client-Role", result.role.name)
                .apply {
                  if (result.role == Role.USER)
                    header("X-User-Id", result.userId.toString())
                  else
                    header("X-Participant-Id", result.participantId.toString())
                }
                .build()
              chain.filter(exchange.mutate().request(mutated).build())
            }

          HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN -> {
            // 상태·헤더·바디 모두 그대로 복사
            exchange.response.statusCode = resp.statusCode()
            exchange.response.headers.putAll(resp.headers().asHttpHeaders())
            exchange.response.writeWith(
              resp.bodyToFlux(DataBuffer::class.java)
            )
          }

          else -> {
            exchange.response.statusCode = resp.statusCode()
            exchange.response.setComplete()
          }
        }
      }
  }

}