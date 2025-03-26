package vottega.user_service.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailSendService(private val mailSender: JavaMailSender) {
  fun sendVerifyEmail(email: String, code: String) {
    val message = SimpleMailMessage().apply {
      setTo(email)
      text = "인증 코드는 $code 입니다.\n 300초 안에 입력하세요"
    }
    mailSender.send(message)
  }
}