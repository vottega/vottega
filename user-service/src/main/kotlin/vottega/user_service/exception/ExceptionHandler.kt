package vottega.user_service.exception

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {
  @ExceptionHandler(DataIntegrityViolationException::class)
  @ResponseStatus(HttpStatus.CONFLICT)
  fun handleDataIntegrityViolation(ex: DataIntegrityViolationException) = "중복된 데이터가 존재합니다."
}