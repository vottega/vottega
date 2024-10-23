package vottega.sse_server.service

import reactor.core.publisher.FluxSink
import reactor.core.publisher.Mono
import java.lang.Void
import java.util.UUID

interface SseService {
    fun enterRoom(roomId: Long, userId : UUID, sink : FluxSink<String>)
    fun exitRoom(roomId: Long, userId : UUID , sink : FluxSink<String>)

    fun broadcastToRoom(roomId: Long, message: String)

}