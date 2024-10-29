package vottega.sse_server.adapt

import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import vottega.sse_server.config.KafkaConfig
import vottega.sse_server.dto.mapper.RoomEventMapper
import vottega.sse_server.repository.SinkRepository

@Component
class KafkaConsumer(val fluxSinkRepository: SinkRepository, val roomEventMapper: RoomEventMapper, private val kafkaConfig : KafkaConfig) {


    private val roomInfoReceiverOptions = getReceiverOptions("room-info-topic")
    private val voteInfoReceiverOptions = getReceiverOptions("vote-info-topic")
    private val shorthandInfoReceiverOptions = getReceiverOptions("shorthand-info-topic")

    private fun getReceiverOptions(topic: String) = ReceiverOptions.create<String, String>(
        mapOf(
            "bootstrap.servers" to kafkaConfig.bootstrapServers,
            "group.id" to kafkaConfig.groupId,
            "key.deserializer" to Class.forName(kafkaConfig.keyDeserializer),
            "value.deserializer" to Class.forName(kafkaConfig.valueDeserializer),
            "auto.offset.reset" to kafkaConfig.autoOffsetReset
        )
    ).subscription(listOf(topic))


    init {
        consumeRoomInfoMessages()
        consumeVoteInfoMessages()
        consumeShorthandInfoMessages()
    }
    private fun consumeRoomInfoMessages() {
        KafkaReceiver.create(roomInfoReceiverOptions)
            .receive()
            .doOnNext { record ->
                val event = roomEventMapper.roomDataToRoomEvent(record.value())
                fluxSinkRepository.getRoomSink(event.roomId).tryEmitNext(event)
            }
            .subscribe()
    }

    private fun consumeVoteInfoMessages() {
        KafkaReceiver.create(voteInfoReceiverOptions)
            .receive()
            .doOnNext { record ->
                val event = roomEventMapper.voteDataToRoomEvent(record.value())
                fluxSinkRepository.getRoomSink(event.roomId).tryEmitNext(event)
            }
            .subscribe()
    }

    private fun consumeShorthandInfoMessages() {
        KafkaReceiver.create(shorthandInfoReceiverOptions)
            .receive()
            .doOnNext { record ->
                val event = roomEventMapper.shorthandDataToRoomEvent(record.value())
                fluxSinkRepository.getRoomSink(event.roomId).tryEmitNext(event)
            }
            .subscribe()
    }
}