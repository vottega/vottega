package vottega.room_service.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.ExceptionTranslationFilter
import vottega.room_service.filter.CustomHeaderAuthenticationFilter

@Configuration
class SecurityConfig {

  @Bean
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