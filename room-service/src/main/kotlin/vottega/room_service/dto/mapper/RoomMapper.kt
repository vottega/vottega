package vottega.room_service.dto.mapper

import main.room_service.avro.RoomAvro
import main.room_service.avro.Status
import org.springframework.stereotype.Component
import vottega.room_service.domain.Room
import vottega.room_service.domain.enumeration.RoomStatus
import vottega.room_service.dto.RoomResponseDTO
import java.time.ZoneOffset

@Component
class RoomMapper(
  private val participantRoleMapper: ParticipantRoleMapper,
  private val participantMapper: ParticipantMapper
) {
  fun toRoomOutDTO(room: Room): RoomResponseDTO {
    return RoomResponseDTO(
      id = room.id ?: throw IllegalStateException("Room ID is null"),
      name = room.roomName,
      ownerId = room.ownerId,
      status = room.status,
      participants =
        room.participantList.map { participant -> participantMapper.toParticipantResponseDTO(participant) },
      createdAt = room.createdAt ?: throw IllegalStateException("createdAt is null"),
      lastUpdatedAt = room.lastUpdatedAt ?: throw IllegalStateException("lastUpdatedAt is null"),
      startedAt = room.startedAt,
      finishedAt = room.finishedAt,
      roles = room.participantRoleList.map { participantRoleMapper.toParticipantRoleDTO(it) }
    )
  }

  fun toRoomAvro(roomResponseDTO: RoomResponseDTO): RoomAvro {
    return RoomAvro.newBuilder()
      .setId(roomResponseDTO.id)
      .setRoomName(roomResponseDTO.name)
      .setOwnerId(roomResponseDTO.ownerId)
      .setStatus(roomStatusToStatus(roomResponseDTO.status))
      .setParticipantRoleList(
        roomResponseDTO.roles.map { participantRoleMapper.toParticipantRoleAvro(it) }
      )
      .setCreatedAt(roomResponseDTO.createdAt.toInstant(ZoneOffset.UTC))
      .setLastUpdatedAt(roomResponseDTO.lastUpdatedAt.toInstant(ZoneOffset.UTC))
      .setStartedAt(roomResponseDTO.startedAt?.toInstant(ZoneOffset.UTC))
      .setFinishedAt(roomResponseDTO.finishedAt?.toInstant(ZoneOffset.UTC))
      .build()
  }

  private fun roomStatusToStatus(roomStatus: RoomStatus): Status {
    return when (roomStatus) {
      RoomStatus.NOT_STARTED -> Status.NOT_STARTED
      RoomStatus.STOPPED -> Status.STOPPED
      RoomStatus.PROGRESS -> Status.PROGRESS
      RoomStatus.FINISHED -> Status.FINISHED
    }
  }
}