package vottega.room_service.dto

import vottega.room_service.domain.enumeration.ParticipantRole

data class ParticipantInfoDTO(val name: String, val position: String, val role: ParticipantRole, val canVote: Boolean)
