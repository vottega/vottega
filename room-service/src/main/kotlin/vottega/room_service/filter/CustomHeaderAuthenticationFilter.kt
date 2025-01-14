package vottega.room_service.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import vottega.room_service.dto.ClientRole
import vottega.room_service.security.CustomParticipantRoleAuthenticationToken
import vottega.room_service.security.CustomUserRoleAuthenticationToken
import java.util.*

class CustomHeaderAuthenticationFilter : OncePerRequestFilter() {
  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    val headerRole = request.getHeader("X-Client-Role")
    val userId = request.getHeader("X-User-Id").toLongOrNull()
    val participantUUID = UUID.fromString(request.getHeader("X-Participant-Id"))
    val role = try {
      ClientRole.valueOf(headerRole)
    } catch (e: IllegalArgumentException) {
      null
    }
    if (role == null || (role == ClientRole.PARTICIPANT && participantUUID == null) || (role == ClientRole.USER && userId == null)) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UnAuthorized User")
    } else {
      val authentication: Authentication = if (role == ClientRole.USER && userId != null) {
        CustomUserRoleAuthenticationToken(userId)
      } else if (role == ClientRole.PARTICIPANT) {
        CustomParticipantRoleAuthenticationToken(participantUUID)
      } else {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UnAuthorized User")
        return
      }
      SecurityContextHolder.getContext().authentication = authentication
      filterChain.doFilter(request, response)
    }
  }
}