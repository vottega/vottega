package vottega.auth_server.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.*

@Component
class JwtUtil {

    private val EXPIRE_TIME = 1000 * 60 * 60 // 1시간
    private val keyPair: KeyPair = generateKeyPair()

    // RSA 키 생성
    private fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(2048)
        return keyGen.generateKeyPair()
    }

    // JWT 발급 (Owner Token)
    fun generateOwnerToken(username: String, password: String): Mono<String> {
        return Mono.fromCallable {
            Jwts.builder()
                .setSubject(username) // username 설정
                .claim("password", password) // password를 Claim에 추가
                .setIssuedAt(Date())
                .setExpiration(Date(System.currentTimeMillis() + EXPIRE_TIME)) // 1시간 만료
                .signWith(keyPair.private, SignatureAlgorithm.RS256) // RSA 서명
                .compact()
        }
    }

    // JWT 발급 (User ID Token)
    fun generateUserIdToken(userId: UUID, roomId: Long): Mono<String> {
        return Mono.fromCallable {
            Jwts.builder()
                .setSubject(userId.toString()) // userId 설정
                .claim("roomId", roomId) // roomId를 Claim에 추가
                .setIssuedAt(Date())
                .setExpiration(Date(System.currentTimeMillis() + EXPIRE_TIME)) // 1시간 만료
                .signWith(keyPair.private, SignatureAlgorithm.RS256) // RSA 서명
                .compact()
        }
    }

    // 공개 키 반환
    fun getPublicKey(): Mono<String> {
        return Mono.fromCallable {
            Base64.getEncoder().encodeToString(keyPair.public.encoded)
        }
    }
}
