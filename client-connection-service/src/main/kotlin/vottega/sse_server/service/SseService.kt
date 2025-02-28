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
  fun enterRoom(roomId: Long, userId: UUID): Flux<RoomEvent> {
    val addUserMono = roomParticipantRepository.addParticipant(roomId, userId)
    val sinkMono = Mono.fromSupplier { sinkRepository.getRoomSink(roomId) }
    val kafkaMono = kafkaProducer.participantProducer(roomId, userId, Action.ENTER)

    return Mono.zip(addUserMono, sinkMono, kafkaMono)
      .flatMapMany { tuple ->
        tuple.t2.asFlux()
      }
  }

  fun exitRoom(roomId: Long, userId: UUID) {
    roomParticipantRepository.removeParticipant(roomId, userId)
      .flatMap { remainingParticipants ->
        if (remainingParticipants.isEmpty()) {
          sinkRepository.removeRoomSink(roomId)
        } else {
          Mono.empty()
        }
      }
      .subscribe()
    kafkaProducer.participantProducer(roomId, userId, Action.EXIT).subscribe()
  }

}