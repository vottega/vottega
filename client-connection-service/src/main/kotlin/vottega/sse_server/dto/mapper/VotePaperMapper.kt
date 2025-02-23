package vottega.sse_server.dto.mapper

import org.springframework.stereotype.Component
import vottega.sse_server.avro.VotePaperAvro
import vottega.sse_server.dto.VotePaperDTO
import java.time.ZoneId

//TODO 이거 해야됨
@Component
class VotePaperMapper {
  fun toVotePaperDTO(votePaperAvro: VotePaperAvro): VotePaperDTO {
    return VotePaperDTO(
      votePaperId = votePaperAvro.id,
      roomId = votePaperAvro.roomId,
      voteId = votePaperAvro.voteId,
      userId = votePaperAvro.userId,
      userName = votePaperAvro.userName,
      votePaperType = avroEnumToVotePaperType(votePaperAvro.votePaperType),
      createdAt = votePaperAvro.createdAt.atZone(ZoneId.systemDefault()).toLocalDateTime(),
      votedAt = votePaperAvro.votedAt?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
    )
  }

  fun avroEnumToVotePaperType(votePaperType: vottega.sse_server.avro.VotePaperType): VotePaperType {
    return when (votePaperType) {
      vottega.sse_server.avro.VotePaperType.YES -> VotePaperType.YES
      vottega.sse_server.avro.VotePaperType.NO -> VotePaperType.NO
      vottega.sse_server.avro.VotePaperType.ABSTAIN -> VotePaperType.ABSTAIN
      vottega.sse_server.avro.VotePaperType.NOT_VOTED -> VotePaperType.NOT_VOTED
    }
  }
}

