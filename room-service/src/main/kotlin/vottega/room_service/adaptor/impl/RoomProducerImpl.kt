package vottega.room_service.adaptor.impl

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import vottega.room_service.adaptor.RoomProducer
import vottega.room_service.domain.Participant
import vottega.room_service.domain.Room
import vottega.room_service.dto.mapper.ParticipantMapper
import vottega.room_service.dto.mapper.RoomMapper

@Component
class RoomProducerImpl(
    private val kafkaTemplate: KafkaTemplate<String, ByteArray>,
    private val roomMapper: RoomMapper,
    private val participantMapper: ParticipantMapper,

    ) : RoomProducer {
    override fun roomEditMessageProduce(room: Room) {
        //TODO 방 수정 이벤트 발생


    }

    override fun participantEditMessageProduce(participant: Participant) {
        //TODO 방에 있는 사람 수정 이벤트 발생

    }
}