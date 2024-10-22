package vottega.sse_server.service.impl

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vottega.sse_server.adapt.KafkaProducer
import vottega.sse_server.service.SseService
import java.util.UUID

@Service
class SseServiceImpl(private val kafkaProducer : KafkaProducer) : SseService {
    override fun enterRoom(roomId: Long, userId: UUID) {
        kafkaProducer.participantEnterProducer(roomId, userId)
    }

    override fun exitRoom(roomId: Long, userId: UUID) {
        kafkaProducer.participantExitProducer(roomId, userId)
    }
}