package vottega.sse_server.cache

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vottega.sse_server.client.RoomClient
import vottega.sse_server.client.RoomResponseDTO

@Service
class UserRoomCacheService(
  private val roomClient: RoomClient,
  private val reactiveRedisTemplate: ReactiveRedisTemplate<Long, Long>
) {
  private val setOps = reactiveRedisTemplate.opsForSet()

  fun isUserIsOwner(userId: Long, roomId: Long): Mono<Boolean> {
    return setOps.isMember(userId, roomId)
      .flatMap { inCache ->
        if (inCache) Mono.just(true)
        else refreshCacheAndCheck(userId, roomId)
      }
  }

  private fun refreshCacheAndCheck(userId: Long, roomId: Long): Mono<Boolean> {
    return reactiveRedisTemplate.delete(userId)
      .thenMany(
        roomClient.getUserRoomList(userId)
          .map(RoomResponseDTO::id)
      )
      .flatMap { id ->
        setOps.add(userId, id)
          .thenReturn(id == roomId)
      }
      .any { it }
  }
}