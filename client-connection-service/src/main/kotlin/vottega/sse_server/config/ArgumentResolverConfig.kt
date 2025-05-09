package vottega.sse_server.config

import ParticipantArgumentResolver
import RoomIdArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer
import vottega.sse_server.argumet_resolver.UserIdArgumentResolver

@Configuration
class ArgumentResolverConfig : WebFluxConfigurer {
  override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
    configurer.addCustomResolver(ParticipantArgumentResolver())
    configurer.addCustomResolver(RoomIdArgumentResolver())
    configurer.addCustomResolver(UserIdArgumentResolver())
  }
}