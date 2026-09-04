package vottega.user_service.cache

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class EmailAuthCacheService(
  private val redisTemplate: RedisTemplate<String, String>,
) {
  fun getEmailAuthCode(email: String) : String?{
    return redisTemplate.opsForValue().get(email)
  }

  fun setEmailAuthCode(email: String, code: String) {
    redisTemplate.opsForValue().set(email, code, 180, TimeUnit.SECONDS)
  }
}