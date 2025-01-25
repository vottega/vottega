package vottega.auth_server.service
//
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//import vottega.auth_server.client.RoomClient
//import vottega.auth_server.client.UserClient
//import vottega.auth_server.jwt.JwtUtil
//import java.util.UUID
//
//@Service
//class AuthService(
//    private val jwtUtil: JwtUtil,
//    private val roomClient: RoomClient,
//    private val userClient: UserClient
//) {
//    fun authenticateUserId(userId: UUID): Mono<String> {
//
//        return roomClient.getUserById(userId)
//            .flatMap { userResponse ->
//                jwtUtil.generateUserIdToken(userResponse.id, userResponse.roomId)
//            }
//    }
//
//    fun authenticateRoomOwner(username: String, password: String): Mono<String> {
//
//        return userClient.authUser(username, password)
//            .flatMap { isAuth ->
//                if (isAuth) {
//                    jwtUtil.generateOwnerToken(username, password) // 비동기 JWT 생성, Mono<String> 반환
//                } else {
//                    Mono.error(RuntimeException("인증 실패")) // 에러 발생
//                }
//            }
//    }
//}