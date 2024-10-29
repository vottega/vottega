package vottega.sse_server.repository

import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.concurrent.ConcurrentHashMap

@Repository
class SinkRepository {
    private val sinkMap = ConcurrentHashMap<Long, Sinks.Many<String>>()

    fun removeRoomSink(roomId: Long) {
        sinkMap.remove(roomId);
    }

    fun getRoomSink(roomId: Long): Flux<String> {
        return sinkMap.computeIfAbsent(roomId) {
            Sinks.many().replay().latest()
        }.asFlux()
    }

}