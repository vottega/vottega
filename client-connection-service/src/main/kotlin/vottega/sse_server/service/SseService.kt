package vottega.sse_server.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vottega.avro.Action
import vottega.sse_server.adapt.KafkaProducer
import vottega.sse_server.dto.RoomEvent
import vottega.sse_server.repository.RoomParticipantRepository
import vottega.sse_server.repository.SinkRepository
import java.util.*


@Service
class SseService(
  private val kafkaProducer: KafkaProducer,
  private val sinkRepository: SinkRepository,
  private val roomParticipantRepository: RoomParticipantRepository
) {
  fun enterRoom(roomId: Long, userId: Any): Flux<RoomEvent> {
    val updateCache = roomParticipantRepository.addParticipant(roomId, userId)
    val sendKafka = if (userId is UUID) {
      kafkaProducer.participantProducer(roomId, userId, Action.ENTER)
    } else {
      Mono.empty()
    }

    return Mono.`when`(updateCache, sendKafka)
      .then(Mono.fromSupplier { sinkRepository.getRoomSink(roomId) })
      .flatMapMany { sink -> sink.asFlux() }
  }


  fun exitRoom(roomId: Long, userId: Any): Mono<Void> {
    val updateCache = roomParticipantRepository.removeParticipant(roomId, userId)
      .flatMap { remainingParticipants ->
        if (remainingParticipants.isEmpty()) {
          sinkRepository.removeRoomSink(roomId)
        } else {
          Mono.empty()
        }
      }
    val sendKafka = if (userId is UUID) {
      kafkaProducer.participantProducer(roomId, userId, Action.EXIT)
    } else {
      Mono.empty()
    }
    return Mono.`when`(updateCache, sendKafka)
  }
}