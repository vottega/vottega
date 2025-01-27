package vottega.auth_server.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vottega.auth_server.client.RoomClient
import vottega.auth_server.client.UserClient
import vottega.auth_server.dto.*
import vottega.auth_server.jwt.JwtUtil
import java.util.UUID

@Service
class AuthService(
    private val jwtUtil: JwtUtil,
    private val roomClient: RoomClient,
    private val userClient: UserClient
) {
    fun authenticateUserId(userId: UUID): Mono<String> {

        return roomClient.getUserById(userId)
            .flatMap { userResponse ->
                jwtUtil.generateUserIdToken(userResponse.id, userResponse.roomId)
            }
    }

    fun authenticateRoomOwner(username: String, password: String): Mono<String> {

        return userClient.authUser(username, password)
            .flatMap { isAuth ->
                if (isAuth) {
                    jwtUtil.generateOwnerToken(username, password) // 비동기 JWT 생성, Mono<String> 반환
                } else {
                    Mono.error(RuntimeException("인증 실패")) // 에러 발생
                }
            }
    }

    fun login(authRequestDTO: AuthRequestDTO): Mono<AuthResponseDTO> {
        return userClient.authUser(authRequestDTO.username, authRequestDTO.password)
            .flatMap { isAuthenticated ->
                if (isAuthenticated) {
                    jwtUtil.generateUserToken(authRequestDTO.username.toLong()) // 사용자 ID를 기반으로 JWT 생성
                        .map { token ->
                            AuthResponseDTO(
                                username = authRequestDTO.username,
                                token = token
                            )
                        }
                } else {
                    Mono.error(IllegalArgumentException("Invalid username or password"))
                }
            }
    }

    fun verify(token: String): Mono<VerifyResponseDTO> {
        return Mono.fromCallable {
            val decodedToken = jwtUtil.decodeToken(token) // 토큰 디코딩 및 검증

            when (decodedToken) {
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
        }.onErrorResume { error ->
            Mono.error(RuntimeException("Token verification failed: ${error.message}", error))
        }
    }
}