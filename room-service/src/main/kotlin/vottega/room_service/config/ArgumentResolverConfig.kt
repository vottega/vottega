package vottega.room_service.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import vottega.room_service.argument.UserIdArgumentResolver

@Configuration
class ArgumentResolverConfig(
  private val userIdArgumentResolver: UserIdArgumentResolver,
) : WebMvcConfigurer {
  override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
    resolvers.add(userIdArgumentResolver)
  }
}