package vottega.sse_server.web

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.LocalTime
import kotlin.time.Duration


@RestController
class SseController {

    @GetMapping("/stream-sse", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamEvents(): Flux<SseEvent> {
        return Flux.interval(Duration.ofSeconds(1))
            .map {
                SseEvent(LocalTime.now(), "Hello, this is an SSE event!")
            }
    }
}