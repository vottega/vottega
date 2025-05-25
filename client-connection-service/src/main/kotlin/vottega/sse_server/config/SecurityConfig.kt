package vottega.sse_server.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import vottega.sse_server.security.CustomServerAuthenticationConverter
import vottega.sse_server.security.PassThroughAuthManager

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

  @Bean
  fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {

    val headerAuthFilter = AuthenticationWebFilter(PassThroughAuthManager()).apply {
      setRequiresAuthenticationMatcher(
        ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, "/api/sse/**")
      )
      setServerAuthenticationConverter(CustomServerAuthenticationConverter())
      setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance())
    }

    return http
      // 세션·쿠키 전혀 안 씀
      .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

      .csrf { it.disable() }
      .httpBasic { it.disable() }
      .formLogin { it.disable() }
      .logout { it.disable() }

      .authorizeExchange { exchanges ->
        exchanges
          .pathMatchers("/api/sse/**").authenticated()
          .anyExchange().permitAll()
      }
      .addFilterAt(headerAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)

      .build()
  }
}