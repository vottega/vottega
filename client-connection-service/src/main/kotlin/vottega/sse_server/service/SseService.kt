package vottega.sse_server.service

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vottega.avro.Action
import vottega.sse_server.adapt.KafkaProducer
import vottega.sse_server.cache.UserRoomCacheService
import vottega.sse_server.dto.RoomEvent
import vottega.sse_server.repository.RoomParticipantRepository
import vottega.sse_server.repository.SinkRepository
import java.util.*


@Service
class SseService(
  private val kafkaProducer: KafkaProducer,
  private val sinkRepository: SinkRepository,
  private val roomParticipantRepository: RoomParticipantRepository,
  private val userRoomCacheService: UserRoomCacheService
) {
  fun enterOwner(roomId: Long, ownerId: Long): Flux<RoomEvent> =
    userRoomCacheService.isUserIsOwner(ownerId, roomId)
      .flatMapMany { isOwner ->
        if (!isOwner)
          Flux.error(
            ResponseStatusException(
              HttpStatus.FORBIDDEN,
              "User $ownerId is not owner of room $roomId"
            )
          )
        else enter(roomId, ownerId)
      }

  /** 진입 ②: 참가자 */
  fun enterParticipant(roomId: Long, participantId: UUID): Flux<RoomEvent> =
    enter(roomId, participantId)

  private fun enter(roomId: Long, client: Any): Flux<RoomEvent> {
    val add = roomParticipantRepository.addParticipant(roomId, client)
    val sendKafka = when (client) {
      is UUID -> kafkaProducer.participantProducer(roomId, client, Action.ENTER)
      else -> Mono.empty()
    }

    return Mono.`when`(add, sendKafka)
      .then(Mono.defer { Mono.just(sinkRepository.getRoomSink(roomId)) })
      .flatMapMany { it.asFlux() }
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