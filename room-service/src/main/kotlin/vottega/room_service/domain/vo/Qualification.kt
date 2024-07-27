package vottega.room_service.domain.vo

import jakarta.persistence.Embeddable
import vottega.room_service.domain.enumeration.ParticipantRole

@Embeddable
data class Qualification(val position : String, val role : ParticipantRole, val canVote : Boolean)
