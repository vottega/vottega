package vottega.sse_server.dto

import vottega.sse_server.dto.mapper.VotePaperType
import java.time.LocalDateTime
import java.util.*

data class VotePaperDTO(
  val votePaperId: Long,
  val voteId: Long,
  val roomId: Long,
  val userId: UUID,
  val userName: String,
  var votePaperType: VotePaperType,
  var createdAt: LocalDateTime,
  var votedAt: LocalDateTime? = null
)