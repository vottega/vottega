package vottega.room_service.web

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import vottega.room_service.dto.ParticipantInfoDTO
import vottega.room_service.dto.ParticipantRoomDTO
import vottega.room_service.dto.RoomResponseDTO
import vottega.room_service.service.RoomService
import java.util.*

@RestController
@RequestMapping("/api/room")
@Tag(name = "Participant Controller", description = "참가자 관련 API")
class ParticipantResource(
  private val roomService: RoomService,
) {
  @PutMapping("/{roomId}/participants")
  @Operation(summary = "참가자 추가", description = "방에 참가자를 추가합니다.")
  @ResponseStatus(HttpStatus.CREATED)
  fun addParticipants(
    @PathVariable roomId: Long,
    @RequestBody ParticipantInfoDTOList: List<ParticipantInfoDTO>
  ): RoomResponseDTO = roomService.addParticipant(roomId, ParticipantInfoDTOList)

  @DeleteMapping("/{roomId}/participants/{participantId}")
  @Operation(summary = "참가자 삭제", description = "방에서 참가자를 삭제합니다.")
  fun removeParticipant(@PathVariable roomId: Long, @PathVariable participantId: UUID): RoomResponseDTO =
    roomService.removeParticipant(roomId, participantId)


  @PatchMapping("/{roomId}/participants/{participantId}")
  @Operation(summary = "참가자 정보 수정", description = "참가자 정보를 수정합니다.")
  fun updateParticipant(
    @PathVariable roomId: Long,
    @PathVariable participantId: UUID,
    @RequestBody participantInfoDTO: ParticipantInfoDTO
  ): RoomResponseDTO = roomService.updateParticipant(roomId, participantId, participantInfoDTO)

  @GetMapping("/participants/{participantId}")
  fun getParticipantRoom(@PathVariable participantId: UUID): ParticipantRoomDTO =
    roomService.getParticipantRoom(participantId)
}