package vottega.auth_server.web

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import vottega.auth_server.dto.*
import vottega.auth_server.service.AuthService

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {
  @PostMapping("/verify")
  fun verify(@RequestBody verifyRequestDTO: VerifyRequestDTO): Mono<VerifyResponseDTO> =
    authService.verify(verifyRequestDTO.token)

  @PostMapping("/user")
  fun createUserToken(@RequestBody userAuthRequestDTO: UserAuthRequestDTO): Mono<AuthResponseDTO> =
    authService.createUserToken(userAuthRequestDTO.id, userAuthRequestDTO.userId)

  @PostMapping("/participant")
  fun authenticateParticipant(
    @RequestBody participantAuthRequestDTO: ParticipantAuthRequestDTO
  ): Mono<AuthResponseDTO> = authService.authenticateParticipantId(participantAuthRequestDTO.participantId)
}