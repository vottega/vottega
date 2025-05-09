package vottega.sse_server.argumet_resolver

import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import vottega.sse_server.security.CustomUserRoleAuthenticationToken

class UserIdArgumentResolver : HandlerMethodArgumentResolver {
  override fun supportsParameter(parameter: MethodParameter): Boolean =
    parameter.hasParameterAnnotation(UserId::class.java)
      && parameter.parameterType == Long::class.java


  override fun resolveArgument(
    parameter: MethodParameter,
    bindingContext: BindingContext,
    exchange: ServerWebExchange
  ): Mono<Any> {
    // 1) 리액티브 시큐리티 컨텍스트에서 Authentication 꺼내기
    return ReactiveSecurityContextHolder.getContext()
      .flatMap { ctx ->
        when (val auth = ctx.authentication) {
          is CustomUserRoleAuthenticationToken -> Mono.just(auth.principal)
          else -> Mono.error(
            ResponseStatusException(HttpStatus.FORBIDDEN, "User 권한이 없습니다.")
          )
        }
      }
  }
}

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class UserId