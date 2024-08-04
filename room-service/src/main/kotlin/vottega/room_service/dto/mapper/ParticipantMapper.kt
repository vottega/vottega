package vottega.room_service.dto.mapper

import org.springframework.stereotype.Component
import vottega.room_service.domain.Participant
import vottega.room_service.dto.ParticipantResponseDTO

@Component
class ParticipantMapper {
    fun toParticipantResponseDTO(participant : Participant) : ParticipantResponseDTO {
        return ParticipantResponseDTO(
            id = participant.id ?: throw IllegalStateException("Participant ID is null"),
            name = participant.name,
            roomId = participant.room.id ?: throw IllegalStateException("Room ID is null"),
            position = participant.qualification.position,
            role = participant.qualification.role,
            canVote = participant.qualification.canVote,
            createdAt = participant.createdAt ?: throw IllegalStateException("createdAt is null"),
            enteredAt = participant.enteredAt,
            lastUpdatedAt = participant.lastUpdatedAt ?: throw IllegalStateException("lastUpdatedAt is null")
        )
    }
}