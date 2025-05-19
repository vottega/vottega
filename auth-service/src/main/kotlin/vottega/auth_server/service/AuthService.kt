package vottega.auth_server.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import vottega.auth_server.client.RoomClient
import vottega.auth_server.client.UserClient
import vottega.auth_server.dto.*
import vottega.auth_server.jwt.JwtUtil
import java.util.*

@Service
class AuthService(
  private val jwtUtil: JwtUtil,
  private val roomClient: RoomClient,
  private val userClient: UserClient
) {
  fun authenticateParticipantId(userId: UUID): Mono<ParticipantAuthResponseDTO> {
    return roomClient.getUserById(userId)
      .subscribeOn(Schedulers.boundedElastic())
      .flatMap { userResponse ->
        jwtUtil.generateParticipantIdToken(userId, userResponse.roomId)
          .publishOn(Schedulers.parallel())
          .map { token ->
            ParticipantAuthResponseDTO(
              token = token,
              roomId = userResponse.roomId,
            )
          }
      }
  }

  fun authenticateUserId(userId: String, password: String): Mono<AuthResponseDTO> {
    return userClient.authenticateUser(userId, password)
      .subscribeOn(Schedulers.boundedElastic())
      .flatMap { userResponse ->
        if (userResponse.verified) {
          jwtUtil.generateUserIdToken(userResponse.id!!, userId)
            .publishOn(Schedulers.parallel())
            .map { token ->
              AuthResponseDTO(
                token = token,
              )
            }
        } else {
          Mono.error(IllegalArgumentException("Invalid credentials"))
        }
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