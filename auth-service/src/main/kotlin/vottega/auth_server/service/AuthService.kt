package vottega.auth_server.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import vottega.auth_server.client.RoomClient
import vottega.auth_server.dto.*
import vottega.auth_server.jwt.JwtUtil
import java.util.UUID

@Service
class AuthService(
  private val jwtUtil: JwtUtil,
  private val roomClient: RoomClient,
) {
  fun authenticateParticipantId(userId: UUID): Mono<AuthResponseDTO> {
    return roomClient.getUserById(userId)
      .subscribeOn(Schedulers.boundedElastic())
      .flatMap { userResponse ->
        jwtUtil.generateParticipantIdToken(userId, userResponse.roomId)
          .publishOn(Schedulers.parallel())
          .map { token ->
            AuthResponseDTO(
              token = token
            )
          }
      }
  }

  fun createUserToken(id: Long, userId: String): Mono<AuthResponseDTO> {
    return jwtUtil.generateUserIdToken(id, userId)
      .publishOn(Schedulers.parallel())
      .map { token ->
        AuthResponseDTO(
          token = token
        )
      }
  }


  fun verify(token: String): Mono<VerifyResponseDTO> {
    return Mono.fromCallable {
      when (val decodedToken = jwtUtil.decodeToken(token)) {
        is JwtResponseDto.JwtParticipantResponseDto -> {
          VerifyResponseDTO(
            role = Role.PARTICIPANT,
            participantId = UUID.fromString(decodedToken.uuid),
            roomId = decodedToken.roomId,
            userId = null
          )
        }

        is JwtResponseDto.JwtUserResponseDto -> {
          VerifyResponseDTO(
            role = Role.USER,
            participantId = null,
            roomId = null,
            userId = decodedToken.userId.toLongOrNull()
          )
        }
      }
    }.subscribeOn(Schedulers.boundedElastic())
  }
}