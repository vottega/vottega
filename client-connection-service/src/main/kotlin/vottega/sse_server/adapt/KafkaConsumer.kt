package vottega.sse_server.adapt

import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class KafkaConsumer {
    fun shortHandConsume(): Flux<String> {
        return Flux.just("Hello", "World")
    }

    fun voteInfoConsume(): Flux<String> {
        return Flux.just("Vote", "Info")
    }

    fun roomInfoConsume(): Flux<String> {
        return Flux.just("Room", "Info")
    }
}