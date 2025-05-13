package vottega.user_service.service

import org.springframework.stereotype.Service
import vottega.user_service.cache.EmailAuthCacheService
import vottega.user_service.dto.DuplicateCheckResponse

@Service
class EmailAuthService(
  private val emailAuthCacheService: EmailAuthCacheService,
  private val emailSendService: EmailSendService
) {
  fun sendEmailAuthCode(email: String) {
    val code = (10000..99999).random().toString()
    emailAuthCacheService.setEmailAuthCode(email, code)

    emailSendService.sendVerifyEmail(email, code)
  }

  fun verifyEmail(email: String, code: String): DuplicateCheckResponse {
    val savedCode = emailAuthCacheService.getEmailAuthCode(email)
    return DuplicateCheckResponse(savedCode == code)
  }
}