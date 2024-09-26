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
            position = participant.position,
            role = participant.participantRole?.role,
            canVote = participant.participantRole?.canVote,
            isEntered = participant.isEntered,
            createdAt = participant.createdAt ?: throw IllegalStateException("createdAt is null"),
            enteredAt = participant.enteredAt,
            lastUpdatedAt = participant.lastUpdatedAt ?: throw IllegalStateException("lastUpdatedAt is null")
        )
    }

    fun toAvro(participantResponseDTO: ParticipantResponseDTO): AvroParticipantResponseDTO {
        return AvroParticipantResponseDTO.newBuilder()
            .setId(participantResponseDTO.id)
            .setName(participantResponseDTO.name)
            .setRoomId(participantResponseDTO.roomId)
            .setPosition(participantResponseDTO.position)
            .setRole(participantResponseDTO.role)
            .setCanVote(participantResponseDTO.canVote)
            .setIsEntered(participantResponseDTO.isEntered)
            .setCreatedAt(participantResponseDTO.createdAt.toInstant(ZoneOffset.UTC).toEpochMilli())
            .setEnteredAt(participantResponseDTO.enteredAt?.toInstant(ZoneOffset.UTC)?.toEpochMilli())
            .setLastUpdatedAt(participantResponseDTO.lastUpdatedAt.toInstant(ZoneOffset.UTC).toEpochMilli())
            .build()
    }
}