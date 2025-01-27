package vottega.auth_server.web

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import vottega.auth_server.dto.AuthRequestDTO
import vottega.auth_server.dto.AuthResponseDTO
import vottega.auth_server.dto.VerifyRequestDTO
import vottega.auth_server.dto.VerifyResponseDTO
import vottega.auth_server.service.AuthService
import java.util.*

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/login")
    fun login(@RequestBody authRequestDTO: AuthRequestDTO): Mono<ResponseEntity<AuthResponseDTO>> {
        return authService.login(authRequestDTO)
            .map { authResponseDTO -> ResponseEntity.ok(authResponseDTO) }
            .onErrorResume { error ->
                Mono.just(ResponseEntity.badRequest().body(null)) // null 대신 에러 메시지만 반환
            }
    }

    @PostMapping("/verify")
    fun verify(@RequestBody verifyRequestDTO: VerifyRequestDTO): Mono<ResponseEntity<VerifyResponseDTO>> {
        return authService.verify(verifyRequestDTO.token)
            .map { verifyResult ->
                ResponseEntity.ok(
                    VerifyResponseDTO(
                        role = verifyResult.role,
                        participantId = verifyResult.participantId,
                        roomId = verifyResult.roomId,
                        userId = verifyResult.userId
                    )
                )
            }
            .onErrorResume { error ->
                Mono.just(ResponseEntity.badRequest().body(null))
            }

    }

    @GetMapping("/user/{userId}")
    fun authenticateUserId(@PathVariable userId: UUID): Mono<ResponseEntity<String>> {
        return authService.authenticateUserId(userId)
            .map { token -> ResponseEntity.ok(token) }
            .onErrorResume { error ->
                Mono.just(ResponseEntity.badRequest().body("Error: ${error.message}"))
            }
    }

    @PostMapping("/room-owner")
    fun authenticateRoomOwner(
        @RequestParam username: String,
        @RequestParam password: String
    ): Mono<ResponseEntity<String>> {
        return authService.authenticateRoomOwner(username, password)
            .map { token -> ResponseEntity.ok(token) }
            .onErrorResume { error ->
                Mono.just(ResponseEntity.badRequest().body("Error: ${error.message}"))
            }
    }
}