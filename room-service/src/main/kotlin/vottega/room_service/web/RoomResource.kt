package vottega.room_service.web

import org.springframework.web.bind.annotation.*
import vottega.room_service.dto.ParticipantRoleDTO
import vottega.room_service.dto.UpdateRoomRequestDTO
import vottega.room_service.dto.RoomResponseDTO
import vottega.room_service.dto.mapper.RoomMapper
import vottega.room_service.service.RoomService

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

    @PutMapping("/{roomId}/role")
    fun putRole(@PathVariable roomId: Long, @RequestBody roleInfo: ParticipantRoleDTO): RoomResponseDTO {
        return roomMapper.toRoomOutDTO(roomService.addRole(roomId, roleInfo))
    }

    @GetMapping("/list/{userId}")
    fun getRoomList(@PathVariable userId: Long): List<RoomResponseDTO> {
        return roomService.getRoomList(userId).map { roomMapper.toRoomOutDTO(it) }
    }

}