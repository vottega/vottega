package vottega.sse_server.service

import reactor.core.publisher.Flux
import vottega.sse_server.dto.RoomEvent
import java.util.UUID

interface SseService {
    fun enterRoom(roomId: Long, userId : UUID) : Flux<RoomEvent>
    fun exitRoom(roomId: Long, userId : UUID)
}