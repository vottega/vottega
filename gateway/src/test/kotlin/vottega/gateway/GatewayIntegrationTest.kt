package vottega.gateway.filter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import vottega.gateway.client.AuthWebClient
import vottega.gateway.client.VerifyResponseDTO
import vottega.gateway.dto.Role
import vottega.gateway.filter.global.TokenCheckGatewayFilterFactory
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Unit tests for {@link TokenCheckGatewayFilterFactory}.
 *
 * Mockito is set to LENIENT strictness so that common stubs created in {@code @BeforeEach}
 * which are unused in some tests (e.g. public-path bypass) do not trigger
 * {@link org.mockito.exceptions.misusing.UnnecessaryStubbingException}.
 */
@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TokenCheckGatewayFilterFactoryTest {

  private lateinit var authWebClient: AuthWebClient
  private lateinit var filterFactory: TokenCheckGatewayFilterFactory

  private val userToken = "user_token"
  private val userResponse = VerifyResponseDTO(
    role = Role.USER,
    participantId = null,
    roomId = null,
    userId = 1L,
  )

  private val participantToken = "participant_token"
  private val participantResponse = VerifyResponseDTO(
    role = Role.PARTICIPANT,
    participantId = UUID.randomUUID(),
    roomId = 1L,
    userId = null,
  )

  private val invalidToken = "invalid_token"

  @BeforeEach
  fun setUp() {
    authWebClient = mock()
    filterFactory = TokenCheckGatewayFilterFactory(authWebClient)

    // Common stubs – may not be used in every test but that's acceptable with LENIENT mode
    whenever(authWebClient.verifyTokenRaw(userToken)).thenReturn(Mono.just(userResponse))
    whenever(authWebClient.verifyTokenRaw(participantToken)).thenReturn(Mono.just(participantResponse))

    val forbiddenEx = WebClientResponseException.create(
      HttpStatus.FORBIDDEN,
      HttpStatus.FORBIDDEN.reasonPhrase,
      HttpHeaders.EMPTY,
      """{""error"":""Forbidden""}""".toByteArray(),
      StandardCharsets.UTF_8,
      null
    )
    whenever(authWebClient.verifyTokenRaw(invalidToken)).thenReturn(Mono.error(forbiddenEx))
  }

  private class CapturingChain : GatewayFilterChain {
    var captured: ServerWebExchange? = null
    override fun filter(exchange: ServerWebExchange): Mono<Void> {
      captured = exchange
      return Mono.empty()
    }
  }

  @Test
  fun `public path bypasses filter`() {
    val exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/user/profile").build())
    val chain = CapturingChain()
    val filter = filterFactory.apply(TokenCheckGatewayFilterFactory.Config())

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()

    assertThat(chain.captured).isNotNull()
    assertThat(chain.captured!!.request.headers["X-Client-Role"]).isNull()
  }

  @Test
  fun `user token adds user headers`() {
    val exchange = MockServerWebExchange.from(
      MockServerHttpRequest.get("/secured").header(HttpHeaders.AUTHORIZATION, "Bearer $userToken").build()
    )
    val chain = CapturingChain()
    val filter = filterFactory.apply(TokenCheckGatewayFilterFactory.Config())

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()

    val mutated = chain.captured!!.request
    assertThat(mutated.headers["X-Client-Role"]).containsExactly(Role.USER.name)
    assertThat(mutated.headers["X-User-Id"]).containsExactly(userResponse.userId.toString())
    assertThat(mutated.headers["X-Participant-Id"]).isNull()
  }

  @Test
  fun `participant token adds participant headers`() {
    val exchange = MockServerWebExchange.from(
      MockServerHttpRequest.get("/secured/resource")
        .header(HttpHeaders.AUTHORIZATION, "Bearer $participantToken")
        .build()
    )
    val chain = CapturingChain()
    val filter = filterFactory.apply(TokenCheckGatewayFilterFactory.Config())

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()

    val mutated = chain.captured!!.request
    assertThat(mutated.headers["X-Client-Role"]).containsExactly(Role.PARTICIPANT.name)
    assertThat(mutated.headers["X-Participant-Id"]).containsExactly(participantResponse.participantId.toString())
    assertThat(mutated.headers["X-Room-Id"]).containsExactly(participantResponse.roomId.toString())
  }

  @Test
  fun `invalid token returns 403 and stops chain`() {
    val exchange = MockServerWebExchange.from(
      MockServerHttpRequest.get("/secured")
        .header(HttpHeaders.AUTHORIZATION, "Bearer $invalidToken")
        .build()
    )
    val chain = CapturingChain()
    val filter = filterFactory.apply(TokenCheckGatewayFilterFactory.Config())

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()

    assertThat(chain.captured).isNull()
    assertThat(exchange.response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
  }

  @Test
  fun `missing authorization header returns 401`() {
    val exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/secured").build())
    val chain = CapturingChain()
    val filter = filterFactory.apply(TokenCheckGatewayFilterFactory.Config())

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()

    assertThat(chain.captured).isNull()
    assertThat(exchange.response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
  }
}
