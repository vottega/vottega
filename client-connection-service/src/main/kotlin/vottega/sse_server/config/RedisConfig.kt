package vottega.sse_server.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext

@Configuration
class RedisConfig {
  @Bean
  fun reactiveRedisLongKeyLongValueTemplate(
    factory: ReactiveRedisConnectionFactory
  ): ReactiveRedisTemplate<Long, Long> {
    val longSer = GenericToStringSerializer(Long::class.java)

    val context = RedisSerializationContext
      .newSerializationContext<Long, Long>(longSer)
      .value(longSer)
      .hashKey(longSer)
      .hashValue(longSer)
      .build()

    return ReactiveRedisTemplate(factory, context)
  }
}