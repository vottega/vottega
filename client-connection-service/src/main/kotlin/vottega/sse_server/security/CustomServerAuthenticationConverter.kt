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
    val roleHeader = headers.getFirst("X-Client-Role") ?: return Mono.empty()

    val role = runCatching { ClientRole.valueOf(roleHeader) }.getOrNull() ?: return Mono.empty()

    return when (role) {
      ClientRole.USER ->
        headers.getFirst("X-User-Id")
          ?.toLongOrNull()
          ?.let { Mono.just(CustomUserRoleAuthenticationToken(it)) }
          ?: Mono.empty()


      ClientRole.PARTICIPANT -> {
        val participantId = headers.getFirst("X-Participant-Id")
          ?.let { runCatching { UUID.fromString(it) }.getOrNull() }
        val roomId = headers.getFirst("X-Room-Id")?.toLongOrNull()

        if (participantId != null && roomId != null)
          Mono.just(CustomParticipantRoleAuthenticationToken(participantId, roomId))
        else Mono.empty()
      }
    }
  }

}