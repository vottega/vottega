package vottega.sse_server.security

import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import vottega.sse_server.dto.ClientRole
import java.util.*

class CustomServerAuthenticationConverter : ServerAuthenticationConverter {
  override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
    val headers = exchange.request.headers
    print(headers)
    val roleHeader = headers.getFirst("X-Client-Role") ?: return Mono.empty()

    val role = runCatching { ClientRole.valueOf(roleHeader) }.getOrNull() ?: return Mono.empty()

    return when (role) {
      ClientRole.USER -> {
        val userId = headers.getFirst("X-User-Id")?.toLongOrNull() ?: return Mono.empty()
        Mono.just(CustomUserRoleAuthenticationToken(userId))
      }

      ClientRole.PARTICIPANT -> {
        val participantId = runCatching { UUID.fromString(headers.getFirst("X-Participant-Id")) }.getOrNull()
          ?: return Mono.empty()
        val roomId = headers.getFirst("X-Room-Id")?.toLongOrNull() ?: return Mono.empty()
        Mono.just(CustomParticipantRoleAuthenticationToken(participantId, roomId))
      }
    }
  }

}