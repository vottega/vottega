package vottega.room_service.web

import org.springframework.web.bind.annotation.*
import vottega.room_service.dto.ParticipantRoleDTO
import vottega.room_service.dto.UpdateRoomRequestDTO
import vottega.room_service.dto.RoomResponseDTO
import vottega.room_service.dto.mapper.RoomMapper
import vottega.room_service.service.RoomService

@RestController("/api/room")
class RoomResource(
    private val roomService: RoomService
) {
    @GetMapping("/{roomId}")
    fun getRoom(@PathVariable roomId: Long): RoomResponseDTO =
        roomService.getRoom(roomId)

    @PatchMapping("/{roomId}")
    fun updateRoom(@PathVariable roomId: Long, @RequestBody roomRequestDTO: UpdateRoomRequestDTO): RoomResponseDTO =
        roomService.updateRoom(roomId, roomRequestDTO.roomName, roomRequestDTO.status)

    @PutMapping("/{roomId}/role")
    fun putRole(@PathVariable roomId: Long, @RequestBody roleInfo: ParticipantRoleDTO): RoomResponseDTO =
        roomService.addRole(roomId, roleInfo)

    @DeleteMapping("/{roomId}/role/{role}")
    fun deleteRole(@PathVariable roomId: Long, @PathVariable role: String): RoomResponseDTO =
        roomService.deleteRole(roomId, role)

    @GetMapping("/list/{userId}")
    fun getRoomList(@PathVariable userId: Long): List<RoomResponseDTO> = roomService.getRoomList(userId)



}