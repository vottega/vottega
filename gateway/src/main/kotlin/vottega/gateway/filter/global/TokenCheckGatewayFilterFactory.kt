import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import vottega.gateway.client.AuthWebClient
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
      .flatMap { result ->
        val mutatedReq = exchange.request.mutate()
          .header("X-Client-Role", result.role.name)
          .apply {
            if (result.role == Role.USER) {
              header("X-User-Id", result.userId.toString())
            } else {
              header("X-Participant-Id", result.participantId.toString())
            }
            result.roomId?.let { header("X-Room-Id", it.toString()) }
          }
          .build()

        chain.filter(exchange.mutate().request(mutatedReq).build())
      }.onErrorResume(WebClientResponseException::class.java) { ex ->
        if (ex.statusCode == HttpStatus.UNAUTHORIZED || ex.statusCode == HttpStatus.FORBIDDEN) {
          exchange.response.statusCode = ex.statusCode
          exchange.response.headers.putAll(ex.headers)
          exchange.response.writeWith(
            Mono.just(exchange.response.bufferFactory().wrap(ex.responseBodyAsByteArray))
          )
        } else {
          Mono.error(ex)
        }
      }
  }

}