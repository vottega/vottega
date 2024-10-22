package vottega.sse_server.adapt

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class KafkaProducer {
    fun participantExitProducer(roomId : Long, userId : UUID) {
        println("Participant Exit Producer")
    }
    fun participantEnterProducer(roomId: Long, userId: UUID) {
        println("Participant Enter Producer")
    }
}