package vottega.room_service.dto.mapper

import org.springframework.stereotype.Component
import vottega.avro.ParticipantRoleAvro
import vottega.room_service.domain.ParticipantRole
import vottega.room_service.dto.ParticipantRoleDTO

@Component
class ParticipantRoleMapper {
  fun toParticipantRoleDTO(participantRole: ParticipantRole): ParticipantRoleDTO {
    return ParticipantRoleDTO(participantRole.role, participantRole.canVote)
  }

  fun toParticipantRoleAvro(role: ParticipantRoleDTO): ParticipantRoleAvro {
    return ParticipantRoleAvro.newBuilder()
      .setRole(role.role)
      .setCanVote(role.canVote)
      .build()
  }

  fun toParticipantRoleDTO(role: ParticipantRoleAvro): ParticipantRoleDTO {
    return ParticipantRoleDTO(role.role, role.canVote)
  }
}