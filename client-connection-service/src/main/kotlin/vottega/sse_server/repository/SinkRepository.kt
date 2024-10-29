package vottega.sse_server.repository

import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import vottega.sse_server.dto.RoomEvent
import java.util.concurrent.ConcurrentHashMap

@Repository
class SinkRepository {
    private val sinkMap = ConcurrentHashMap<Long, Sinks.Many<RoomEvent>>()

    fun removeRoomSink(roomId: Long) {
        sinkMap.remove(roomId); // TODO 아직 연결되어 있는 사람이 있으면 REMOVE 안하기
    }

    fun getRoomSink(roomId: Long): Flux<RoomEvent> {
        return sinkMap.computeIfAbsent(roomId) {
            Sinks.many().replay().latest()
        }.asFlux()
    }

}