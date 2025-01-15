package vottega.room_service.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import vottega.room_service.dto.ClientRole

class CustomUserRoleAuthenticationToken(
  private val userId: Long,
) : AbstractAuthenticationToken(listOf(SimpleGrantedAuthority(ClientRole.USER.name))) {
  init {
    isAuthenticated = true
  }

  override fun getCredentials(): Any = ClientRole.USER.name
  override fun getPrincipal(): Any = userId
}