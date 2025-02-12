package vottega.room_service.web

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import vottega.room_service.dto.CreateRoomRequestDTO
import vottega.room_service.dto.ParticipantRoleDTO
import vottega.room_service.dto.RoomResponseDTO
import vottega.room_service.dto.UpdateRoomRequestDTO
import vottega.room_service.service.RoomService

@RestController
@RequestMapping("/api/room")
@Tag(name = "Room Controller", description = "방 관련 API")
class RoomResource(
  private val roomService: RoomService
) {
  @PostMapping
  @Operation(summary = "방 생성", description = "방을 생성합니다.")
  @ResponseStatus(HttpStatus.CREATED)
  fun createRoom(@RequestBody createRoomRequestDTO: CreateRoomRequestDTO): RoomResponseDTO =
    roomService.createRoom(
      createRoomRequestDTO.roomName,
      createRoomRequestDTO.ownerId,
      createRoomRequestDTO.participantRoleList
    )

  @GetMapping("/{roomId}")
  @Operation(summary = "방 조회", description = "방을 조회합니다.")
  fun getRoom(@PathVariable roomId: Long): RoomResponseDTO =
    roomService.getRoom(roomId)

  @PatchMapping("/{roomId}")
  @Operation(summary = "방 정보 수정", description = "방 정보를 수정합니다.")
  fun updateRoom(@PathVariable roomId: Long, @RequestBody roomRequestDTO: UpdateRoomRequestDTO): RoomResponseDTO =
    roomService.updateRoom(roomId, roomRequestDTO.roomName, roomRequestDTO.status)

  @PutMapping("/{roomId}/role")
  @Operation(summary = "역할 추가", description = "방에 역할을 추가합니다.")
  @ResponseStatus(HttpStatus.CREATED)
  fun putRole(@PathVariable roomId: Long, @RequestBody roleInfo: ParticipantRoleDTO): RoomResponseDTO =
    roomService.addRole(roomId, roleInfo)

  @DeleteMapping("/{roomId}/role/{role}")
  @Operation(summary = "역할 삭제", description = "방에서 역할을 삭제합니다.")
  fun deleteRole(@PathVariable roomId: Long, @PathVariable role: String): RoomResponseDTO =
    roomService.deleteRole(roomId, role)

  @GetMapping("/list/{userId}")
  @Operation(summary = "방 목록 조회", description = "사용자의 방 목록을 조회합니다.")
  fun getRoomList(@PathVariable userId: Long): List<RoomResponseDTO> = roomService.getRoomList(userId)


}