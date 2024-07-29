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
) {
    @GetMapping("/{roomId}")
    fun getRoom(@PathVariable roomId: Long): RoomResponseDTO {
        return roomMapper.toRoomOutDTO(roomService.getRoom(roomId))
    }

    @PatchMapping("/{roomId}")
    fun updateRoom(@PathVariable roomId: Long, @RequestBody roomRequestDTO: UpdateRoomRequestDTO): RoomResponseDTO {
        return roomMapper.toRoomOutDTO(roomService.updateRoom(roomId, roomRequestDTO.roomName, roomRequestDTO.status))
    }

}