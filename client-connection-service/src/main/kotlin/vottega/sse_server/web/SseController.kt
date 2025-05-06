package vottega.sse_server.web

import ParticipantId
import RoomId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import vottega.sse_server.dto.RoomEvent
import vottega.sse_server.service.SseService
import java.util.*


@RestController
@Tag(name = "SSE Controller", description = "SSE 관련 API")
class SseController(private val sseService: SseService) {

  @GetMapping("/api/sse/room", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
  @Operation(summary = "SSE 연결", description = "SSE 연결을 위한 API")
  fun connectToRoom(@RoomId roomId: Long, @ParticipantId userId: UUID): Flux<RoomEvent> { //TODO UUID는 security로 받기
    return sseService.enterRoom(roomId, userId).doOnCancel {
      sseService.exitRoom(roomId, userId)
    }
  }

//  @GetMapping("/api/sse/room/{roomId}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
//  @Operation(summary = "SSE 연결", description = "SSE 연결을 위한 API")
//  fun connectToRoom(@UserId userId: Long, @PathVariable roomId: Long): Flux<RoomEvent> { //TODO UUID는 security로 받기
//    return sseService.enterRoom(roomId, userId).doOnCancel {
//      sseService.exitRoom(roomId, userId)
//    }
//  }

  @GetMapping("/api/sse/room/{roomId}/{participantId}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
  @Operation(summary = "SSE 연결", description = "SSE 연결을 위한 API")
  @Profile("local")
  fun connectToRoomLocal(
    @PathVariable roomId: Long,
    @PathVariable participantId: UUID
  ): Flux<RoomEvent> { //TODO UUID는 security로 받기
    return sseService.enterRoom(roomId, participantId).doOnCancel {
      sseService.exitRoom(roomId, participantId)
    }
  }
}