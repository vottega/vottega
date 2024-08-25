package vottega.room_service.web

import org.springframework.web.bind.annotation.*
import vottega.room_service.dto.RoomResponseDTO
import vottega.room_service.dto.mapper.RoomMapper
import vottega.room_service.dto.ParticipantInfoDTO
import vottega.room_service.service.RoomService
import java.util.*

@RestController("/api/room")
class ParticipantResource(
    private val roomService: RoomService,
    private val roomMapper: RoomMapper
) {
    @PutMapping("/{roomId}/participants")
    fun addParticipants(
        @PathVariable roomId: Long,
        @RequestBody ParticipantInfoDTOList: List<ParticipantInfoDTO>
    ): RoomResponseDTO = roomService.addParticipant(roomId, ParticipantInfoDTOList)

    @DeleteMapping("/{roomId}/participants/{participantId}")
    fun removeParticipant(@PathVariable roomId: Long, @PathVariable participantId: UUID): RoomResponseDTO =
        roomService.removeParticipant(roomId, participantId)


    @PatchMapping("/api/room/{roomId}/participants/{participantId}")
    fun updateParticipant(
        @PathVariable roomId: Long,
        @PathVariable participantId: UUID,
        @RequestBody participantInfoDTO: ParticipantInfoDTO
    ): RoomResponseDTO = roomService.updateParticipant(roomId, participantId, participantInfoDTO)

}