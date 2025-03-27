package vottega.user_service.web

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import vottega.user_service.dto.*
import vottega.user_service.service.EmailAuthService
import vottega.user_service.service.UserService

@RestController
@RequestMapping("/api/user")
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

  @PostMapping("/check/userId")
  fun checkUserIdDuplication(@RequestBody userIdCheckRequest: UserIdCheckRequest) =
    userService.checkUserIdDuplication(userIdCheckRequest.userId)

  @PostMapping("/check/email")
  fun checkEmailDuplication(@RequestBody emailCheckRequest: EmailCheckRequest) =
    userService.checkEmailDuplication(emailCheckRequest.email)

  @PostMapping("/validate")
  fun validateCode(@RequestBody emailValidateRequest: EmailValidateRequest) =
    emailAuthService.verifyEmail(emailValidateRequest.email, emailValidateRequest.emailAuthCode)

  @PostMapping("/send")
  fun sendEmail(@RequestBody emailSendRequest: EmailSendRequest) =
    emailAuthService.sendEmailAuthCode(emailSendRequest.email)
}