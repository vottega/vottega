package vottega.auth_server.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ResponseStatus
import reactor.core.publisher.Mono
import vottega.auth_server.dto.JwtResponseDto
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SignatureException
import java.util.*


@Component
class JwtUtil {

  private val EXPIRE_TIME = 1000 * 60 * 60 * 24
  private val keyPair: KeyPair = generateKeyPair()

  // RSA 키 생성
  private fun generateKeyPair(): KeyPair {
    val keyGen = KeyPairGenerator.getInstance("RSA")
    keyGen.initialize(2048)
    return keyGen.generateKeyPair()
  }

  fun generateUserIdToken(id: Long, userId: String): Mono<String> {
    return Mono.fromCallable {
      Jwts.builder()
        .setSubject(id.toString())
        .claim("userId", userId)
        .claim("role", "user")
        .setIssuedAt(Date())
        .setExpiration(Date(System.currentTimeMillis() + EXPIRE_TIME)) // 1시간 만료
        .signWith(keyPair.private, SignatureAlgorithm.RS256) // RSA 서명
        .compact()
    }
  }

  fun generateParticipantIdToken(userId: UUID, roomId: Long): Mono<String> {
    return Mono.fromCallable {
      Jwts.builder()
        .setSubject(userId.toString())
        .claim("roomId", roomId)
        .claim("role", "participant")
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

  fun decodeToken(token: String): JwtResponseDto {
    try {
      // Parse the token and extract claims
      val claims: Claims = Jwts.parserBuilder()
        .setSigningKey(keyPair.public)
        .build()
        .parseClaimsJws(token)
        .body

      // Check if the token is expired
      if (claims.expiration.before(Date())) {
        throw CredentialsExpiredException("Token expired")
      }

      // Extract role and return data based on the role
      return when (claims["role"] as? String) {

        "participant" -> JwtResponseDto.JwtParticipantResponseDto(
          uuid = claims.subject ?: throw BadCredentialsException("Missing UUID"),
          roomId = (claims["roomId"] as? Number)?.toLong()
            ?: throw BadCredentialsException("Missing or invalid roomId")
        )

        "user" -> JwtResponseDto.JwtUserResponseDto(
          id = (claims.subject ?: throw BadCredentialsException("Missing id")).toLong(),
          userId = (claims["userId"] as? String) ?: throw BadCredentialsException("Missing userId")
        )

        else -> throw BadCredentialsException("Invalid role in token")
      }
    } catch (e: SignatureException) {
      throw BadCredentialsException("Invalid token signature", e)
    } catch (e: Exception) {
      throw BadCredentialsException("Invalid or expired token", e)
    }
  }


  // 공개 키 반환
  fun getPublicKey(): String {
    return Base64.getEncoder().encodeToString(keyPair.public.encoded)
  }
}


@ResponseStatus(HttpStatus.UNAUTHORIZED)
class BadCredentialsException : RuntimeException {
  constructor(message: String) : super(message)
  constructor(message: String, cause: Throwable) : super(message, cause)
}

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class CredentialsExpiredException(msg: String) : RuntimeException(msg)