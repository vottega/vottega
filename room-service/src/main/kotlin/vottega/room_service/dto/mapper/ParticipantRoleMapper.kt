package vottega.room_service.dto.mapper

import org.springframework.stereotype.Component
import vottega.room_service.domain.ParticipantRole
import vottega.room_service.dto.ParticipantRoleDTO

@Component
class ParticipantRoleMapper {
    fun toParticipantRoleDTO(participantRole: ParticipantRole): ParticipantRoleDTO {
        return ParticipantRoleDTO(participantRole.role, participantRole.canVote)
    }
}