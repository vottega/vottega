package vottega.sse_server

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import vottega.sse_server.dto.RoomEvent
import vottega.sse_server.dto.RoomEventType
import vottega.sse_server.service.SseService
import java.util.*

@SpringBootTest(
  properties = [
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
  ],
)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class SseSecurityIntegrationTest {

  @Autowired
  private lateinit var webTestClient: WebTestClient

  // 실제 SseService 대신에 이 MockBean 이 주입됩니다.
  @MockBean
  private lateinit var sseService: SseService

  val roomId = 1L
  val participantId = UUID.randomUUID()


  @BeforeEach
  fun setUp() {
    val dummy = RoomEvent(type = RoomEventType.ROOM_INFO, data = "roomInfo")
    given(sseService.enterParticipant(any(), any()))
      .willReturn(Flux.just(dummy))
  }

  @Test
  fun `PARTICIPANT 헤더 주입되면 200 OK, 첫 이벤트 검증`() {
    webTestClient.get()
      .uri("/sse/room")
      .header("X-Client-Role", "PARTICIPANT")
      .header("X-Participant-Id", participantId.toString())
      .header("X-Room-Id", roomId.toString())
      .accept(MediaType.TEXT_EVENT_STREAM)
      .exchange()
      .expectStatus().isOk
  }

  @Test
  fun `헤더가 하나라도 없으면 403 Forbidden or 401 Unauthorized`() {
    webTestClient.get()
      .uri("/sse/room")
      .accept(MediaType.TEXT_EVENT_STREAM)
      .exchange()
      .expectStatus().isUnauthorized

    webTestClient.get()
      .uri("/sse/room")
      .header("X-Client-Role", "PARTICIPANT")
      .header("X-Room-Id", roomId.toString())
      .accept(MediaType.TEXT_EVENT_STREAM)
      .exchange()
      .expectStatus().isUnauthorized

    webTestClient.get()
      .uri("/sse/room")
      .header("X-Client-Role", "PARTICIPANT")
      .header("X-Participant-Id", participantId.toString())
      .accept(MediaType.TEXT_EVENT_STREAM)
      .exchange()
      .expectStatus().isUnauthorized

  }
}