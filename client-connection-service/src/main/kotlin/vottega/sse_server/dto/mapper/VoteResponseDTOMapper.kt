package vottega.sse_server.dto.mapper

import org.springframework.stereotype.Service
import vottega.sse_server.avro.Result
import vottega.sse_server.avro.VoteAvro
import vottega.sse_server.avro.VoteStatus
import vottega.sse_server.dto.VoteResponseDTO
import java.time.ZoneId

@Service
class VoteResponseDTOMapper(private val fractionMapper: FractionMapper) {

  fun toVoteResponseDTO(voteAvro: VoteAvro): VoteResponseDTO {
    return VoteResponseDTO(
      id = voteAvro.id,
      roomId = voteAvro.roomId,
      agendaName = voteAvro.agendaName,
      voteName = voteAvro.voteName,
      status = voteStatusToStatus(voteAvro.voteStatus),
      createdAt = voteAvro.createdAt.atZone(ZoneId.systemDefault()).toLocalDateTime(),
      passRate = fractionMapper.toFractionVO(voteAvro.passRate),
      minParticipantNumber = voteAvro.minParticipantNumber,
      minParticipantRate = fractionMapper.toFractionVO(voteAvro.minParticipantRate),
      isSecret = voteAvro.isSecret,
      reservedStartTime = voteAvro.reservedStartTime.atZone(ZoneId.systemDefault()).toLocalDateTime(),
      voteAction = voteAvro.voteAction,
      startedAt = voteAvro.startedAt?.atZone(ZoneId.systemDefault())?.toLocalDateTime(),
      finishedAt = voteAvro.finishedAt?.atZone(ZoneId.systemDefault())?.toLocalDateTime(),
      lastUpdatedAt = voteAvro.lastUpdatedAt.atZone(ZoneId.systemDefault()).toLocalDateTime(),
      result = voteResultToVoteResult(voteAvro.voteResult),
      yesNum = voteAvro.yesNum,
      noNum = voteAvro.noNum,
      abstainNum = voteAvro.abstainNum
    )
  }


  fun voteStatusToStatus(status: VoteStatus): Status {
    return when (status) {
      VoteStatus.CREATED -> Status.CREATED
      VoteStatus.STARTED -> Status.STARTED
      VoteStatus.ENDED -> Status.ENDED
    }
  }

  fun voteResultToVoteResult(result: Result): VoteResult {
    return when (result) {
      Result.PASSED -> VoteResult.PASSED
      Result.NOT_DECIDED -> VoteResult.NOT_DECIDED
      Result.REJECTED -> VoteResult.REJECTED
    }
  }
}

enum class Status {
  CREATED, STARTED, ENDED
}

enum class VotePaperType {
  YES, NO, ABSTAIN, NOT_VOTED
}

enum class VoteResult {
  PASSED, REJECTED, NOT_DECIDED
}