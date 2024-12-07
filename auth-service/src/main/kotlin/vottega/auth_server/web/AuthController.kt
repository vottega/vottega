package vottega.auth_server.web

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import vottega.auth_server.jwt.JwtUtil

@RestController
class AuthController(private val jwtUtil : JwtUtil, private val authService: AuthService) {
    @GetMapping("/public-key")
    fun getPublicKey(): String {
        return jwtUtil.getPublicKey()
    }

    @PostMapping("/auth/login")
    fun login(@RequestBody request: AuthRequest): Mono<ResponseEntity<AuthResponse>> {

    }

    @PostMapping("/auth/verify")
    fun getVerifyUUID(@RequestBody request: VerifyRequest): Mono<ResponseEntity<VerifyResponse>> {

    }
}