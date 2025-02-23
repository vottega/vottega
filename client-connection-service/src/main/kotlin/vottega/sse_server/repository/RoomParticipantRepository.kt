package vottega.sse_server.repository

import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
class RoomParticipantRepository {
  private val roomParticipantMap = mutableMapOf<Long, MutableSet<UUID>>()

  fun addParticipant(roomId: Long, userId: UUID): Mono<MutableSet<UUID>> {
    return Mono.fromSupplier {
      val participants = roomParticipantMap.computeIfAbsent(roomId) { mutableSetOf() }
      participants.add(userId)
      participants
    }
  }

  fun removeParticipant(roomId: Long, userId: UUID): Mono<MutableSet<UUID>> {
    return Mono.fromSupplier { roomParticipantMap[roomId]?.apply { remove(userId) } ?: mutableSetOf() }
  }
}