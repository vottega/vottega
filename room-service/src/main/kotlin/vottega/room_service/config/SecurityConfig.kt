package vottega.room_service.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.ExceptionTranslationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import vottega.room_service.filter.CustomHeaderAuthenticationFilter

@Configuration
class SecurityConfig {

  @Bean
  @Profile("local")
  @Order(1)
  fun webSecurityCustomizer(http: HttpSecurity): SecurityFilterChain {

    val multipleMatchers = OrRequestMatcher(
      AntPathRequestMatcher("/v3/api-docs/**"),
      AntPathRequestMatcher("/swagger-ui/**"),
      AntPathRequestMatcher("/api")
    )
    http
      // 이 체인은 /api/** 로 들어오는 요청만 매칭
      .securityMatcher(multipleMatchers)
      .csrf { it.disable() }
      .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
      .authorizeHttpRequests {
        // 여긴 전부 허용
        it.anyRequest().permitAll()
      }
    // CustomHeaderAuthenticationFilter 등록하지 않음
    return http.build()
  }

  @Bean
  @Order(2)
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http
      .csrf { it.disable() }
      .logout { it.disable() }
      .anonymous { it.disable() }
      .logout { it.disable() }
      .formLogin { it.disable() }
      .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // 세션 관리 비활성화
      .authorizeHttpRequests {
        it.requestMatchers("/error").permitAll() // /error 경로는 인증 없이 허용
          .anyRequest().authenticated() // 그 외의 요청은 인증 필요
      }

    http.addFilterAfter(CustomHeaderAuthenticationFilter(), ExceptionTranslationFilter::class.java)

    return http.build()
  }
}