package vottega.room_service.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import vottega.room_service.dto.ClientRole
import java.util.*

class CustomParticipantRoleAuthenticationToken(
  private val participantUUID: UUID,
) : AbstractAuthenticationToken(listOf(SimpleGrantedAuthority(ClientRole.PARTICIPANT.name))) {
  init {
    isAuthenticated = true
  }

  override fun getCredentials(): Any = ClientRole.PARTICIPANT.name
  override fun getPrincipal(): Any = participantUUID
}