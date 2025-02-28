package vottega.sse_server.adapt

import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import vottega.avro.ParticipantAvro
import vottega.avro.RoomAvro
import vottega.avro.VoteAvro
import vottega.avro.VotePaperAvro
import vottega.sse_server.config.KafkaCommonConfig
import vottega.sse_server.dto.RoomEvent
import vottega.sse_server.dto.RoomEventType
import vottega.sse_server.dto.mapper.ParticipantMapper
import vottega.sse_server.dto.mapper.RoomMapper
import vottega.sse_server.dto.mapper.VotePaperMapper
import vottega.sse_server.dto.mapper.VoteResponseDTOMapper
import vottega.sse_server.repository.SinkRepository

@Component
class KafkaConsumer(
  val fluxSinkRepository: SinkRepository,
  val roomMapper: RoomMapper,
  val participantMapper: ParticipantMapper,
  val voteResponseDTOMapper: VoteResponseDTOMapper,
  val votePaperMapper: VotePaperMapper,
  val kafkaCommonConfig: KafkaCommonConfig
) {
  private val roomInfoReceiverOptions = getReceiverOptions<RoomAvro>("room")
  private val participantInfoReceiverOptions = getReceiverOptions<ParticipantAvro>("participant")
  private val voteInfoReceiverOptions = getReceiverOptions<VoteAvro>("vote-info-topic")
  private val votePaperInfoReceiverOptions = getReceiverOptions<VotePaperAvro>("vote-paper-info-topic")

  private fun <T> getReceiverOptions(topic: String) = ReceiverOptions.create<String, T>(
    kafkaCommonConfig.commonConsumerConfig()
  ).subscription(listOf(topic))


  init {
    consumeRoomInfoMessages()
    consumeVoteInfoMessages()
    consumeParticipantInfoMessages()
    consumeVotePaperInfoMessages()
  }

  private fun consumeRoomInfoMessages() {
    KafkaReceiver.create(roomInfoReceiverOptions)
      .receive()
      .map { roomMapper.toRoomDTO(it.value()) }
      .doOnNext {
        fluxSinkRepository.getRoomSink(it.id).tryEmitNext(RoomEvent(type = RoomEventType.ROOM_INFO, it))
      }
      .subscribe()
  }

  private fun consumeVoteInfoMessages() {
    KafkaReceiver.create(voteInfoReceiverOptions)
      .receive()
      .map { voteResponseDTOMapper.toVoteResponseDTO(it.value()) }
      .doOnNext {
        fluxSinkRepository.getRoomSink(it.roomId).tryEmitNext(RoomEvent(type = RoomEventType.VOTE_INFO, it))
      }
      .subscribe()
  }

  private fun consumeParticipantInfoMessages() {
    KafkaReceiver.create(participantInfoReceiverOptions)
      .receive()
      .map { participantMapper.toParticipantResponseDTO(it.value()) }
      .doOnNext {
        fluxSinkRepository.getRoomSink(it.roomId).tryEmitNext(RoomEvent(type = RoomEventType.PARTICIPANT_INFO, it))
      }
      .subscribe()
  }

  private fun consumeVotePaperInfoMessages() {
    KafkaReceiver.create(votePaperInfoReceiverOptions)
      .receive()
      .map { votePaperMapper.toVotePaperDTO(it.value()) }
      .doOnNext {
        fluxSinkRepository.getRoomSink(it.roomId).tryEmitNext(RoomEvent(type = RoomEventType.VOTE_PAPER_INFO, it))
      }
      .subscribe()
  }
}