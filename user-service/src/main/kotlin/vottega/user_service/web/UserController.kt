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
      name = userCreateRequest.name,
      userId = userCreateRequest.userId,
      email = userCreateRequest.email,
      password = userCreateRequest.password,
      emailAuthCode = userCreateRequest.emailAuthCode
    )
  }

  @PostMapping("/check/userId")
  fun checkUserIdDuplication(@RequestBody userIdCheckRequest: UserIdCheckRequest): DuplicateCheckResponse =
    userService.checkUserIdDuplication(userIdCheckRequest.userId)

  @PostMapping("/check/email")
  fun checkEmailDuplication(@RequestBody emailCheckRequest: EmailCheckRequest): DuplicateCheckResponse =
    userService.checkEmailDuplication(emailCheckRequest.email)

  @PostMapping("/validate")
  fun validateCode(@RequestBody emailValidateRequest: EmailValidateRequest): DuplicateCheckResponse =
    emailAuthService.verifyEmail(emailValidateRequest.email, emailValidateRequest.emailAuthCode)

  @PostMapping("/send")
  fun sendEmail(@RequestBody emailSendRequest: EmailSendRequest) =
    emailAuthService.sendEmailAuthCode(emailSendRequest.email)


  @PostMapping("/login")
  fun login(@RequestBody loginRequest: LoginRequest) =
    userService.validateUser(loginRequest.userId, loginRequest.password)
}