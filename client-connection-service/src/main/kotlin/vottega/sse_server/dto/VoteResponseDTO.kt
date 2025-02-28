package vottega.sse_server.dto

import vottega.avro.VoteAction
import vottega.sse_server.dto.mapper.Status
import vottega.sse_server.dto.mapper.VoteResult
import java.time.LocalDateTime

data class VoteResponseDTO(
  val id: Long,
  val roomId: Long,
  val agendaName: String,
  val voteName: String,
  val status: Status,
  val passRate: FractionVO,
  val minParticipantNumber: Int,
  val minParticipantRate: FractionVO,
  val reservedStartTime: LocalDateTime,
  val isSecret: Boolean,
  val yesNum: Int,
  val noNum: Int,
  val abstainNum: Int,
  val createdAt: LocalDateTime,
  val result: VoteResult,
  val startedAt: LocalDateTime? = null,
  val finishedAt: LocalDateTime? = null,
  val lastUpdatedAt: LocalDateTime,
  val voteAction: VoteAction,
)
