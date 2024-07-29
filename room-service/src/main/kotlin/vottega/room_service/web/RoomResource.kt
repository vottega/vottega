package vottega.room_service.web

import org.springframework.web.bind.annotation.*
import vottega.room_service.dto.UpdateRoomRequestDTO
import vottega.room_service.dto.RoomResponseDTO
import vottega.room_service.dto.mapper.ParticipantMapper
import vottega.room_service.dto.mapper.RoomMapper
import vottega.room_service.dto.participantInfoDTO
import vottega.room_service.service.RoomService
import java.util.*

@RestController("/api/room")
class RoomResource(
    private val roomService: RoomService,
    private val roomMapper: RoomMapper,
    private val participantMapper: ParticipantMapper
) {
    @GetMapping("/{roomId}")
    fun getRoom(@PathVariable roomId: Long): RoomResponseDTO {
        return roomMapper.toRoomOutDTO(roomService.getRoom(roomId))
    }

    @PatchMapping("/{roomId}")
    fun updateRoom(@PathVariable roomId: Long, @RequestBody roomRequestDTO: UpdateRoomRequestDTO): RoomResponseDTO {
        return roomMapper.toRoomOutDTO(roomService.updateRoom(roomId, roomRequestDTO.roomName, roomRequestDTO.status))
    }

    @PutMapping("/{roomId}/participants")
    fun addParticipants(@PathVariable roomId: Long, @RequestBody participantInfoDTOs: List<participantInfoDTO>): RoomResponseDTO {
        return roomMapper.toRoomOutDTO(roomService.addParticipant(roomId, participantInfoDTOs))
    }

    @DeleteMapping("/{roomId}/participants/{participantId}")
    fun removeParticipant(@PathVariable roomId: Long, @PathVariable participantId: UUID): RoomResponseDTO {
        return roomMapper.toRoomOutDTO(roomService.removeParticipant(roomId, participantId))
    }
}