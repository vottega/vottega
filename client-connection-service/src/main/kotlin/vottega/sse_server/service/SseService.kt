package vottega.sse_server.service

import reactor.core.publisher.Mono
import java.lang.Void
import java.util.UUID

interface SseService {
    fun enterRoom(roomId: Long, userId : UUID)
    fun exitRoom(roomId: Long, userId : UUID)

    fun consumeShortHand(): Mono<String>
    fun consumeVoteInfo(): Mono<String>
    fun consumeRoomInfo(): Mono<String>

}