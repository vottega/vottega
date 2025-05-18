package vottega.user_service.config

import feign.RequestInterceptor
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.oauth2.client.*
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.web.client.RestTemplate

@Configuration
class FeignOAuth2Config(
  private val clientRegistrationRepository: ClientRegistrationRepository,
  private val authorizedClientService: OAuth2AuthorizedClientService
) {
  @Bean
  fun clientCredentialsManager(): OAuth2AuthorizedClientManager {
    val authProvider = OAuth2AuthorizedClientProviderBuilder.builder()
      .clientCredentials()
      .build()

    val manager = AuthorizedClientServiceOAuth2AuthorizedClientManager(
      clientRegistrationRepository, authorizedClientService
    )
    manager.setAuthorizedClientProvider(authProvider)
    return manager
  }

  @Bean
  fun oauth2FeignRequestInterceptor(clientCredentialsManager: OAuth2AuthorizedClientManager): RequestInterceptor =
    RequestInterceptor { request ->
      val clientRegistrationId = "service-b"
      val principal = UsernamePasswordAuthenticationToken("service-b", "N/A", emptyList())
      val authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
        .principal(principal)
        .build()

      val authorizedClient = clientCredentialsManager.authorize(authorizeRequest)
        ?: throw IllegalStateException("Cannot obtain Oauth2 access token")

      val token = authorizedClient.accessToken.tokenValue
      request.header("Authorization", "Bearer $token")
    }

  @Bean
  @LoadBalanced
  fun loadBalancedRestTemplate(): RestTemplate = RestTemplate()
}