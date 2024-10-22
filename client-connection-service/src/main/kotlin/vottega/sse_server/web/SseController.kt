package vottega.sse_server.web

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.LocalTime
import kotlin.time.Duration


@RestController
class SseController(private val kafkaConsumer: KafkaConsumer) {

    @GetMapping("/sse/kafka", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamKafkaMessages(): Flux<String> {
        // 여러 Kafka Consumer를 하나의 Flux로 병합
        return Flux.merge(
            kafkaConsumer.consumeRoomMessages(),
            kafkaConsumer.consumeVoteMessages(),
            kafkaConsumer.consumeShorthandMessages()
        )
            .map { message ->
                "data: $message\n\n" // SSE 포맷에 맞게 메시지 변환
            }
            .doOnCancel {
                // 클라이언트가 연결을 끊었을 때 처리할 로직
                println("Client disconnected from SSE")
            }
    }
}