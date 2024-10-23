package vottega.sse_server.service.impl

import org.springframework.stereotype.Service
import reactor.core.publisher.FluxSink
import reactor.core.publisher.Mono
import vottega.sse_server.adapt.KafkaProducer
import vottega.sse_server.repository.FluxSinkRepository
import vottega.sse_server.service.SseService
import java.util.UUID

@Service
class SseServiceImpl(private val kafkaProducer: KafkaProducer, private val fluxSinkRepository: FluxSinkRepository) :
    SseService {
    override fun enterRoom(roomId: Long, userId: UUID, sink: FluxSink<String>) {
        kafkaProducer.participantEnterProducer(roomId, userId)
        fluxSinkRepository.addClient(roomId, sink)
    }

    override fun exitRoom(roomId: Long, userId: UUID, sink: FluxSink<String>) {
        kafkaProducer.participantExitProducer(roomId, userId)
        fluxSinkRepository.removeClient(roomId, sink)
    }

    override fun broadcastToRoom(roomId: Long, message: String) {
        val clients = fluxSinkRepository.getClients(roomId)
        clients.forEach { it.next(message) }
    }
}