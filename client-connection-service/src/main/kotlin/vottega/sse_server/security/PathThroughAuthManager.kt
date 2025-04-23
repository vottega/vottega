package vottega.sse_server.security

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono

class PassThroughAuthManager : ReactiveAuthenticationManager {
  override fun authenticate(authentication: Authentication): Mono<Authentication> =
    Mono.just(authentication.apply { isAuthenticated = true })
}