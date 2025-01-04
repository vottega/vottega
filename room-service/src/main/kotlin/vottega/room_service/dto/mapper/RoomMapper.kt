package vottega.room_service.dto.mapper

import main.room_service.avro.RoomAvro
import org.springframework.stereotype.Component
import vottega.room_service.domain.Room
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
      participants = room.participantList.map { participant -> participantMapper.toParticipantResponseDTO(participant) },
      createdAt = room.createdAt ?: throw IllegalStateException("createdAt is null"),
      lastUpdatedAt = room.lastUpdatedAt ?: throw IllegalStateException("lastUpdatedAt is null"),
      startedAt = room.startedAt,
      finishedAt = room.finishedAt
    )
  }

  fun toRoomAvro(roomResponseDTO: RoomResponseDTO): RoomAvro {
    return RoomAvro.newBuilder()
      .setId(roomResponseDTO.id)
      .setRoomName(roomResponseDTO.name)
      .setParticipantRoleList(
        roomResponseDTO.participants.map { participantRoleMapper.toParticipantRoleAvro(it.participantRole) }
      )
      .setCreatedAt(roomResponseDTO.createdAt.toInstant(ZoneOffset.UTC))
      .setLastUpdatedAt(roomResponseDTO.lastUpdatedAt.toInstant(ZoneOffset.UTC))
      .setStartedAt(roomResponseDTO.startedAt?.toInstant(ZoneOffset.UTC))
      .setFinishedAt(roomResponseDTO.finishedAt?.toInstant(ZoneOffset.UTC))
      .build()
  }
}