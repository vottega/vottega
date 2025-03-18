package vottega.user_service.service

import org.springframework.stereotype.Service
import vottega.user_service.adapt.EmailProducer
import vottega.user_service.cache.EmailAuthCacheService

@Service
class EmailAuthService(private val emailAuthCacheService: EmailAuthCacheService,
                       private val emailSendService: EmailSendService){
  fun sendEmailAuthCode(email : String){
    val code = (10000..99999).random().toString()
    emailAuthCacheService.setEmailAuthCode(email, code)

    emailSendService.sendEmail(email, code)
  }

  fun verifyEmail(email : String, code : String) : Boolean{
    val savedCode = emailAuthCacheService.getEmailAuthCode(email)
    return savedCode == code
  }
}