package vottega.sse_server.security

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.test.StepVerifier
import vottega.avro.Action
import vottega.sse_server.adapt.KafkaProducer
import vottega.sse_server.cache.UserRoomCacheService
import vottega.sse_server.dto.RoomEvent
import vottega.sse_server.dto.RoomEventType
import vottega.sse_server.repository.RoomParticipantRepository
import vottega.sse_server.repository.SinkRepository
import vottega.sse_server.service.SseService
import java.util.*


@ExtendWith(MockitoExtension::class)
class SseServiceTest {

  @Mock
  private lateinit var kafkaProducer: KafkaProducer

  @Mock
  private lateinit var sinkRepository: SinkRepository

  @Mock
  private lateinit var participantRepo: RoomParticipantRepository

  @Mock
  private lateinit var userRoomCache: UserRoomCacheService

  private lateinit var sseService: SseService

  @BeforeEach
  fun setUp() {
    sseService = SseService(
      kafkaProducer,
      sinkRepository,
      participantRepo,
      userRoomCache
    )
  }

  /** 테스트용 더미 Sink를 만들어, 구독 시점에 이벤트 하나와 complete 시그널을 방출 */
  private fun dummySink(event: RoomEvent): Sinks.Many<RoomEvent> {
    val sink = Sinks.many().unicast().onBackpressureBuffer<RoomEvent>()
    sink.tryEmitNext(event)
    sink.tryEmitComplete()
    return sink
  }

  @Test
  fun `enterParticipant should add participant, send kafka and emit sink events`() {
    val roomId = 99L
    val participantId = UUID.randomUUID()
    val event = RoomEvent(RoomEventType.PARTICIPANT_INFO, "mock")

    // 모킹: 캐시 추가, Kafka 전송, Sink 반환
    whenever(participantRepo.addParticipant(roomId, participantId))
      .thenReturn(Mono.empty())
    whenever(kafkaProducer.participantProducer(roomId, participantId, Action.ENTER))
      .thenReturn(Mono.empty())
    whenever(sinkRepository.getRoomSink(roomId))
      .thenReturn(dummySink(event))

    StepVerifier.create(sseService.enterParticipant(roomId, participantId))
      .expectNext(event)
      .verifyComplete()

    verify(participantRepo).addParticipant(roomId, participantId)
    verify(kafkaProducer).participantProducer(roomId, participantId, Action.ENTER)
    verify(sinkRepository).getRoomSink(roomId)
  }
}