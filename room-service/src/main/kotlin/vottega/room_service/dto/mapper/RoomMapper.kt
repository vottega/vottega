package vottega.room_service.dto.mapper

import org.springframework.stereotype.Component
import vottega.room_service.domain.Room
import vottega.room_service.dto.RoomResponseDTO
import vottega.room_service.avro.RoomResponseDTO as AvroRoomResponseDTO
import vottega.room_service.avro.ParticipantResponseDTO as AvroParticipantResponseDTO

@Component
class RoomMapper {
    fun toRoomOutDTO(room : Room) : RoomResponseDTO {
        return RoomResponseDTO(
            id = room.id ?: throw IllegalStateException("Room ID is null"),
            name = room.roomName,
            participants = room.participantList.map { participant -> ParticipantMapper().toParticipantResponseDTO(participant) },
            createdAt = room.createdAt ?: throw IllegalStateException("createdAt is null"),
            lastUpdatedAt = room.lastUpdatedAt ?: throw IllegalStateException("lastUpdatedAt is null"),
            startedAt = room.startedAt,
            finishedAt = room.finishedAt
        )
    }

    fun toAvro(roomResponseDTO: RoomResponseDTO): AvroRoom {
        return AvroRoomResponseDTO.newBuilder()
            .setId(roomResponseDTO.id)
            .setName(roomResponseDTO.name)
            .setParticipants(
                roomResponseDTO.participants.map { toAvro(it) }
            )
            .setCreatedAt(roomResponseDTO.createdAt.toInstant(ZoneOffset.UTC).toEpochMilli())
            .setLastUpdatedAt(roomResponseDTO.lastUpdatedAt.toInstant(ZoneOffset.UTC).toEpochMilli())
            .setStartedAt(roomResponseDTO.startedAt?.toInstant(ZoneOffset.UTC)?.toEpochMilli())
            .setFinishedAt(roomResponseDTO.finishedAt?.toInstant(ZoneOffset.UTC)?.toEpochMilli())
            .build()
    }
}