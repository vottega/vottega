package vottega.user_service.web

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import vottega.user_service.dto.EmailValidateRequest
import vottega.user_service.dto.UserCreateRequest
import vottega.user_service.service.EmailAuthService
import vottega.user_service.service.UserService

@RestController("/user")
class UserController(private val userService: UserService, private val emailAuthService: EmailAuthService) {

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  fun createUser(@RequestBody userCreateRequest: UserCreateRequest) {
    userService.createUser(
      userCreateRequest.name,
      userCreateRequest.userId,
      userCreateRequest.password,
      userCreateRequest.email,
      userCreateRequest.emailAuthCode
    )
  }

  @PostMapping
  fun validateCode(@RequestBody emailValidateRequest: EmailValidateRequest) =
    emailAuthService.verifyEmail(emailValidateRequest.email, emailValidateRequest.emailAuthCode)
  
}