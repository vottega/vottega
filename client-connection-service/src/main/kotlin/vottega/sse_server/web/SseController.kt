package vottega.sse_server.web

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import vottega.sse_server.dto.RoomEvent
import vottega.sse_server.service.SseService
import java.util.UUID


@RestController
class SseController(private val sseService: SseService) {

    @GetMapping("/sse/room/{roomId}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun connectToRoom(@PathVariable roomId: Long, userId : UUID): Flux<RoomEvent> {
        return sseService.enterRoom(roomId, userId).doOnCancel {
            sseService.exitRoom(roomId, userId)
        }
    }
}