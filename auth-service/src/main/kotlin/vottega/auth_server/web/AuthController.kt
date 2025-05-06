package vottega.auth_server.web

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import vottega.auth_server.dto.*
import vottega.auth_server.service.AuthService

@RestController
@RequestMapping("/api/auth")
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
  ): Mono<ParticipantAuthResponseDTO> = authService.authenticateParticipantId(participantAuthRequestDTO.participantId)
}