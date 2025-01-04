package vottega.room_service.exception

import java.util.*

class ParticipantNotFoundException(participantId: UUID) : RuntimeException("$participantId : Participant Not Found")