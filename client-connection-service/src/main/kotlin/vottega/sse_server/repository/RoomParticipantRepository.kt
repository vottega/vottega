package vottega.sse_server.repository

import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

@Repository
class RoomParticipantRepository {
  private val roomParticipantMap =
    ConcurrentHashMap<Long, MutableSet<Any>>()

  fun addParticipant(roomId: Long, userId: Any): Mono<Set<Any>> =
    Mono.fromCallable {
      roomParticipantMap
        .computeIfAbsent(roomId) { CopyOnWriteArraySet() }
        .apply { add(userId) }
        .toSet()
    }.subscribeOn(Schedulers.parallel())

  fun removeParticipant(roomId: Long, userId: Any): Mono<Set<Any>> =
    Mono.fromCallable {
      roomParticipantMap[roomId]
        ?.apply { remove(userId) }
        ?.takeIf { it.isNotEmpty() }
        ?.toSet()
        ?: emptySet()
    }.subscribeOn(Schedulers.parallel())
}