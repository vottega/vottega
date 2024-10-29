package vottega.sse_server.service.impl

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import vottega.sse_server.adapt.KafkaProducer
import vottega.sse_server.dto.RoomEvent
import vottega.sse_server.repository.SinkRepository
import vottega.sse_server.service.SseService
import java.util.UUID

@Service
class SseServiceImpl(private val kafkaProducer: KafkaProducer, private val sinkRepository: SinkRepository) :
    SseService {
    override fun enterRoom(roomId: Long, userId: UUID) : Flux<RoomEvent> {
        kafkaProducer.participantEnterProducer(roomId, userId)
        return sinkRepository.getRoomSink(roomId)
    }

    override fun exitRoom(roomId: Long, userId: UUID) {
        kafkaProducer.participantExitProducer(roomId, userId)
        sinkRepository.removeRoomSink(roomId)
    }

}