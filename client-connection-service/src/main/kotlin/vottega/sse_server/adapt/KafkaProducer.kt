package vottega.sse_server.adapt

import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.SenderRecord
import vottega.sse_server.avro.Action
import vottega.sse_server.avro.ParticipantAvro
import vottega.sse_server.config.KafkaCommonConfig
import vottega.sse_server.dto.mapper.ParticipantMapper
import java.util.*

@Component
class KafkaProducer(
  private val kafkaCommonConfig: KafkaCommonConfig,
  private val participantMapper: ParticipantMapper,
  mapper: ParticipantMapper
) {

  private val kafkaSender: KafkaSender<Long, ParticipantAvro> by lazy {
    val props = kafkaCommonConfig.commonProducerConfig()
    val senderOptions = SenderOptions.create<Long, ParticipantAvro>(props)
    KafkaSender.create(senderOptions)
  }

  fun participantProducer(roomId: Long, userId: UUID, roomAction: Action): Mono<Boolean> {
    val value = participantMapper.toParticipantAvro(roomId, userId, Action.EXIT)
    val senderRecord = SenderRecord.create(
      "participant", //TODO 다른 곳에서 공통으로 토픽 이름 관리하기
      null,
      null,
      roomId,
      value,
      roomId,
    )

    return kafkaSender.send(Mono.just(senderRecord))
      .then(Mono.just(true))
      .onErrorReturn(false)
  }

  @PreDestroy
  fun shutdown() {
    kafkaSender.close()
  }
}