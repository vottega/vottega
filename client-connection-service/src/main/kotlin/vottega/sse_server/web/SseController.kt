package vottega.sse_server.web

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import vottega.security.security.ParticipantId
import vottega.security.security.RoomId
import vottega.security.security.UserId
import vottega.sse_server.dto.RoomEvent
import vottega.sse_server.service.SseService
import java.util.*


@RestController
@Tag(name = "SSE Controller", description = "SSE 관련 API")
class SseController(private val sseService: SseService) {

  @GetMapping("/api/sse/room", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
  @Operation(summary = "SSE 연결", description = "SSE 연결을 위한 API")
  fun connectToRoom(
    @RoomId roomId: Long,
    @ParticipantId participantId: UUID
  ): Flux<RoomEvent> {
    return sseService.enterParticipant(roomId, participantId).doOnCancel {
      sseService.exitRoom(roomId, participantId).subscribeOn(Schedulers.boundedElastic()).subscribe()
    }
  }

  @GetMapping("/api/sse/room/{roomId}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
  @Operation(summary = "SSE 연결", description = "SSE 연결을 위한 API")
  fun connectToRoom(@PathVariable roomId: Long, @UserId userId: Long): Flux<RoomEvent> { //TODO UUID는 security로 받기
    return sseService.enterOwner(roomId, userId).doOnCancel {
      sseService.exitRoom(roomId, userId).subscribeOn(Schedulers.boundedElastic()).subscribe()
    }
  }
}