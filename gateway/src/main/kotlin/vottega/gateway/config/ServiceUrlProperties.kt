package vottega.gateway.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceUrlProperties {
  @Value("\${service.room}")
  lateinit var roomServiceUrl: String

  @Value("\${service.vote}")
  lateinit var voteServiceUrl: String

  @Value("\${service.user}")
  lateinit var userServiceUrl: String

  @Value("\${service.auth}")
  lateinit var authServiceUrl: String
}