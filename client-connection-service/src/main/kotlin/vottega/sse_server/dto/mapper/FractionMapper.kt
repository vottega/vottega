package vottega.sse_server.dto.mapper

import org.springframework.stereotype.Component
import vottega.avro.FractionAvro
import vottega.sse_server.dto.FractionVO


@Component
class FractionMapper {
  fun toFractionVO(fractionAvro: FractionAvro): FractionVO {
    return FractionVO(
      numerator = fractionAvro.numerator,
      denominator = fractionAvro.denominator
    )
  }
}