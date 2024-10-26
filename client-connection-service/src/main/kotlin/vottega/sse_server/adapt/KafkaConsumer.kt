package vottega.sse_server.adapt

import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import vottega.sse_server.service.SseService

@Component
class KafkaConsumer(val sseService: SseService) {
    fun shortHandConsume() {
        return sseService.broadcastToRoom(1, "shortHandConsume")
    }

    fun voteInfoConsume() {
        return sseService.broadcastToRoom(1, "voteInfoConsume")
    }

    fun roomInfoConsume() {
        return sseService.broadcastToRoom(1, "roomInfoConsume")
    }
}