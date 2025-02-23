package vottega.sse_server.repository

import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import vottega.sse_server.dto.RoomEvent
import java.util.concurrent.ConcurrentHashMap

@Repository
class SinkRepository {
  private val sinkMap = ConcurrentHashMap<Long, Sinks.Many<RoomEvent>>()

  fun removeRoomSink(roomId: Long): Mono<Void> {
    return Mono.fromRunnable { sinkMap.remove(roomId) }
  }

  fun getRoomSink(roomId: Long): Sinks.Many<RoomEvent> {
    return sinkMap.computeIfAbsent(roomId) {
      Sinks.many().multicast().directBestEffort<RoomEvent>()
    }
  }

}