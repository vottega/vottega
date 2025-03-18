package vottega.user_service.exception

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {
  @ExceptionHandler(DataIntegrityViolationException::class)
  fun handleDataIntegrityViolation(ex: DataIntegrityViolationException): ResponseEntity<String> {
    return ResponseEntity.status(HttpStatus.CONFLICT).body("중복된 데이터가 존재합니다.")
  }
}