package vottega.user_service.service

import org.springframework.stereotype.Service
import vottega.user_service.adapt.EmailProducer

@Service
class EmailSendService(private val emailProducer: EmailProducer){
    fun sendEmail(email: String, code: String) {
      val text = "Your verification code is $code"
        emailProducer.sendEmail(email, text)
    }
}