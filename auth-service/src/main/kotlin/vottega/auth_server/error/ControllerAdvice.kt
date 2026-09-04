package vottega.auth_server.error

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import reactor.core.publisher.Mono

data class ErrorResponse(
  val status: Int,
  val error: String,
  val message: String
)


@RestControllerAdvice
class ControllerAdvice {
  @ExceptionHandler(IllegalArgumentException::class)
  fun handleInvalidArgument(ex: IllegalArgumentException): Mono<ResponseEntity<ErrorResponse>> {
    val body = ErrorResponse(
      status = HttpStatus.UNAUTHORIZED.value(),
      error = "Invalid credentials",
      message = ex.message ?: "Invalid credentials"
    )
    return Mono.just(
      ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
    )
  }
}