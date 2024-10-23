package vottega.sse_server.web

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import vottega.sse_server.adapt.KafkaConsumer
import vottega.sse_server.service.SseService
import java.time.LocalTime
import java.util.UUID
import kotlin.time.Duration


@RestController
class SseController(private val sseService: SseService) {

    @GetMapping("/sse/room/{roomId}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamKafkaMessages(@PathVariable roomId: Long, userId: UUID): Flux<String> { // TODO Argument Resolver로 변경

        return Flux.create<String> { sink ->
            sseService.enterRoom(roomId, userId, sink)
            sink.onCancel {
                sseService.exitRoom(roomId, userId, sink)
            }
        }
    }
}