package vottega.room_service.dto.mapper

import org.springframework.stereotype.Component
import vottega.room_service.domain.Room
import vottega.room_service.dto.RoomResponseDTO

@Component
class RoomMapper {
    fun toRoomOutDTO(room : Room) : RoomResponseDTO {
        return RoomResponseDTO(
            id = room.id ?: throw IllegalStateException("Room ID is null"),
            name = room.roomName,
            participants = room.participantList.map { participant -> ParticipantMapper().toParticipantInfoDTO(participant) },
            createdAt = room.createdAt ?: throw IllegalStateException("createdAt is null"),
            lastUpdatedAt = room.lastUpdatedAt ?: throw IllegalStateException("lastUpdatedAt is null"),
            startedAt = room.startedAt,
            finishedAt = room.finishedAt
        )
    }
}