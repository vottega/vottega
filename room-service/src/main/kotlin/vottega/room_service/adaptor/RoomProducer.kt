package vottega.room_service.adaptor

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import vottega.room_service.domain.Participant
import vottega.room_service.domain.Room


@Service
interface RoomProducer {

    fun roomEditMessageProduce(room: Room)
    fun participantEditMessageProduce(participant: Participant)

}