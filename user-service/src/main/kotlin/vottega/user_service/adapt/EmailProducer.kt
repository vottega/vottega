package vottega.user_service.adapt

import org.springframework.stereotype.Component

@Component
class EmailProducer {
  fun sendEmail(email: String, text: String) {
    println("Send email to $email with code $text")
  }
}