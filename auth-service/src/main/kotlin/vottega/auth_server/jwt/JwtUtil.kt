package vottega.auth_server.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import vottega.auth_server.dto.JwtResponseDto
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SignatureException
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

    /**
     * JWT 발급 (Participant Token)
     *
     * @param uuid : 참여자 고유 ID (UUID).
     * @param roomId : 방 고유 ID (Long).
     * @return Mono<String> : JWT token as a String.
     */
    fun generateParticipantToken(uuid: UUID, roomId: Long): String {
        return Jwts.builder()
            .setSubject(uuid.toString()) // 참여자 고유 ID
            .claim("roomId", roomId) // 방 ID
            .claim("role", "participant") // 역할 설정
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + EXPIRE_TIME)) // 1시간 만료
            .signWith(keyPair.private, SignatureAlgorithm.RS256) // RSA 서명
            .compact()
    }


    /**
     * JWT 발급 (User Token)
     *
     * @param userId The ID of the user (Long).
     * @return Mono<String> : JWT token as a String.
     */
    fun generateUserToken(userId: Long): Mono<String> {
        return Mono.fromCallable {
            Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", "user") // 역할 설정
                .setIssuedAt(Date())
                .setExpiration(Date(System.currentTimeMillis() + EXPIRE_TIME)) // 1시간 만료
                .signWith(keyPair.private, SignatureAlgorithm.RS256) // RSA 서명
                .compact()
        }
    }

    fun verifyToken(token: String): Mono<Boolean> {
        return Mono.fromCallable {
            Jwts.parserBuilder()
                .setSigningKey(keyPair.public)
                .build()
                .parseClaimsJws(token)
            true
        }
    }

    /**
     * Decodes and validates a JWT token.
     *
     * @param token The JWT token to decode (String).
     * @return A Map containing decoded information.
     * @throws IllegalArgumentException If the token is invalid, expired, or parsing fails.
     */
    /**
     * Decodes and validates a JWT token.
     *
     * @param token The JWT token to decode (String).
     * @return A JwtResponseDto object based on the role (JwtParticipantResponseDto or JwtUserResponseDto).
     * @throws IllegalArgumentException If the token is invalid, expired, or parsing fails.
     */
    fun decodeToken(token: String): JwtResponseDto {
        try {
            // Parse the token and extract claims
            val claims: Claims = Jwts.parserBuilder()
                .setSigningKey(keyPair.public) // Verify the token signature using the public key
                .build()
                .parseClaimsJws(token)
                .body

            // Check if the token is expired
            if (claims.expiration.before(Date())) {
                throw IllegalArgumentException("Token expired") // Throw an error if the token has expired
            }

            // Extract role and return data based on the role
            return when (claims["role"] as? String) {

                "participant" -> JwtResponseDto.JwtParticipantResponseDto(
                    uuid = claims.subject ?: throw IllegalArgumentException("Missing UUID"),
                    roomId = (claims["roomId"] as? Number)?.toLong()
                        ?: throw IllegalArgumentException("Missing or invalid roomId")
                )
                "user" -> JwtResponseDto.JwtUserResponseDto(
                    userId = claims.subject ?: throw IllegalArgumentException("Missing userId")
                )
                else -> throw IllegalArgumentException("Invalid role in token")
            }
        } catch (e: SignatureException) {
            throw IllegalArgumentException("Invalid token signature", e) // Handle invalid token signature
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid or expired token", e) // Handle other parsing or validation errors
        }
    }


    // 공개 키 반환
    fun getPublicKey(): Mono<String> {
        return Mono.fromCallable {
            Base64.getEncoder().encodeToString(keyPair.public.encoded)
        }
    }



}
