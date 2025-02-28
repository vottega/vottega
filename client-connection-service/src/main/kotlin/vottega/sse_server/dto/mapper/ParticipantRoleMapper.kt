package vottega.sse_server.dto.mapper

import org.springframework.stereotype.Component
import vottega.avro.ParticipantRoleAvro
import vottega.sse_server.dto.ParticipantRoleDTO

@Component
class ParticipantRoleMapper {
  fun toParticipantRoleDTO(role: ParticipantRoleAvro): ParticipantRoleDTO {
    return ParticipantRoleDTO(role.role, role.canVote)
  }
}