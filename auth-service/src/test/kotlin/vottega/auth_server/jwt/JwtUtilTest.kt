package vottega.auth_server.jwt

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import vottega.auth_server.dto.JwtResponseDto
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.*

class JwtUtilTest {

 private lateinit var jwtUtil: JwtUtil
 private lateinit var keyPair: KeyPair

 @BeforeEach
 fun setUp() {
  keyPair = generateKeyPair()
  jwtUtil = JwtUtil()
 }

 @Test
 fun `generateOwnerToken should generate valid token`() {
  val username = "testUser"
  val password = "testPassword"

  val token = jwtUtil.generateOwnerToken(username, password).block()
  assertNotNull(token)
  println("Generated Owner Token: $token")
 }

 @Test
 fun `generateUserIdToken should generate valid token`() {
  val userId = UUID.randomUUID()
  val roomId = 12345L

  val token = jwtUtil.generateUserIdToken(userId, roomId).block()
  assertNotNull(token)
  println("Generated User ID Token: $token")
 }

 @Test
 fun `generateParticipantToken should generate valid token`() {
  val uuid = UUID.randomUUID()
  val roomId = 54321L

  val token = jwtUtil.generateParticipantToken(uuid, roomId)
  assertNotNull(token)
  println("Generated Participant Token: $token")
 }

 @Test
 fun `generateUserToken should generate valid token`() {
  val userId = 12345L

  val token = jwtUtil.generateUserToken(userId).block()
  assertNotNull(token)
  println("Generated User Token: $token")
 }

 @Test
 fun `verifyToken should return true for valid token`() {
  val uuid = UUID.randomUUID()
  val roomId = 12345L

  val token = jwtUtil.generateParticipantToken(uuid, roomId)
  val isValid = jwtUtil.verifyToken(token!!).block()
  assertTrue(isValid!!)
 }

 @Test
 fun `decodeToken should decode Participant Token`() {
  val uuid = UUID.randomUUID()
  val roomId = 12345L

  val token = jwtUtil.generateParticipantToken(uuid, roomId)
  val decoded = jwtUtil.decodeToken(token)

  assertTrue(decoded is JwtResponseDto.JwtParticipantResponseDto)
  val participantDto = decoded as JwtResponseDto.JwtParticipantResponseDto
  assertEquals(uuid.toString(), participantDto.uuid)
  assertEquals(roomId, participantDto.roomId)
 }

 @Test
 fun `decodeToken should decode User Token`() {
  val userId = 12345L

  val token = jwtUtil.generateUserToken(userId).block()
  val decoded = jwtUtil.decodeToken(token!!)

  assertTrue(decoded is JwtResponseDto.JwtUserResponseDto)
  val userDto = decoded as JwtResponseDto.JwtUserResponseDto
  assertEquals(userId.toString(), userDto.userId)
 }

 @Test
 fun `getPublicKey should return valid public key`() {
  val publicKey = jwtUtil.getPublicKey()
  assertNotNull(publicKey)
  println("Public Key: $publicKey")
 }

 // Helper 메서드: RSA KeyPair 생성
 private fun generateKeyPair(): KeyPair {
  val keyGen = KeyPairGenerator.getInstance("RSA")
  keyGen.initialize(2048)
  return keyGen.generateKeyPair()
 }
}