package vottega.user_service.service

import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ActiveProfiles
import vottega.user_service.domain.User
import vottega.user_service.dto.UserCreateRequest
import vottega.user_service.repository.UserRepository

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceTest {

  @Autowired
  lateinit var userService: UserService

  @MockBean
  lateinit var emailAuthService: EmailAuthService

  @Autowired
  lateinit var userRepository: UserRepository

  var existUser: User = User(username = "기존이름", userId = "기존아이디", email = "기존이메일", password = "기존비밀번호")

  @BeforeEach
  fun setUp() {
    userRepository.save(existUser)
  }


  @Test
  @DisplayName("유저 생성 테스트")
  fun createUser() {
    val newUserRequest =
      UserCreateRequest(name = "새이름", userId = "새아이디", email = "새이메일", password = "새비밀번호", emailAuthCode = "인증코드")
    val newUser = userService.createUser(
      newUserRequest.name,
      newUserRequest.userId,
      newUserRequest.email,
      newUserRequest.password,
      newUserRequest.emailAuthCode
    )
    val foundUser = userRepository.findByUserId(newUser.userId)
    assertThat(foundUser).isNotNull
    assertThat(foundUser?.username).isEqualTo(newUser.username)
    assertThat(foundUser?.userId).isEqualTo(newUser.userId)
    assertThat(foundUser?.email).isEqualTo(newUser.email)
  }

  @Test
  @DisplayName("비밀번호 테스트")
  fun passwordTest() {
    val newUserRequest =
      UserCreateRequest(name = "새이름", userId = "새아이디", email = "새이메일", password = "새비밀번호", emailAuthCode = "인증코드")
    val newUser = userService.createUser(
      newUserRequest.name,
      newUserRequest.userId,
      newUserRequest.email,
      newUserRequest.password,
      newUserRequest.emailAuthCode
    )
    assertThat(userService.validateUser(newUserRequest.userId, newUserRequest.password)).isTrue
  }

  @Test
  @DisplayName("같은 이름 생성 시에 실패 테스트")
  fun createUserAndFailByDuplicateName() {
    val newUserRequest =
      UserCreateRequest(
        name = existUser.username,
        userId = "새아이디",
        email = "새이메일",
        password = "새비밀번호",
        emailAuthCode = "인증코드"
      )
    assertThrows<DataIntegrityViolationException> {
      userService.createUser(
        newUserRequest.name,
        newUserRequest.userId,
        newUserRequest.email,
        newUserRequest.password,
        newUserRequest.emailAuthCode
      )
    }
  }

  @Test
  @DisplayName("같은 유저 아이디 생성 시에 실패 테스트")
  fun createUserAndFailByDuplicateUserID() {
    val newUserRequest =
      UserCreateRequest(
        name = "새이름",
        userId = existUser.userId,
        email = "새이메일",
        password = "새비밀번호",
        emailAuthCode = "인증코드"
      )
    assertThrows<DataIntegrityViolationException> {
      userService.createUser(
        newUserRequest.name,
        newUserRequest.userId,
        newUserRequest.email,
        newUserRequest.password,
        newUserRequest.emailAuthCode
      )
    }
  }

  @Test
  @DisplayName("같은 유저 이메일 생성 시에 실패 테스트")
  fun createUserAndFailByDuplicateEmail() {
    val newUserRequest =
      UserCreateRequest(
        name = "새이름",
        userId = "새아이디",
        email = existUser.email,
        password = "새비밀번호",
        emailAuthCode = "인증코드"
      )
    assertThrows<DataIntegrityViolationException> {
      userService.createUser(
        newUserRequest.name,
        newUserRequest.userId,
        newUserRequest.email,
        newUserRequest.password,
        newUserRequest.emailAuthCode
      )
    }
  }

  @Test
  fun checkUserIdDuplication() {
    assertThat(userService.checkUserIdDuplication("새아이디")).isFalse
    assertThat(userService.checkUserIdDuplication(existUser.userId)).isTrue
  }

  @Test
  fun checkEmailDuplication() {
    assertThat(userService.checkEmailDuplication("새이메일")).isFalse
    assertThat(userService.checkEmailDuplication(existUser.email)).isTrue
  }


  @Test
  @DisplayName("유저 이름 업데이트 테스트")
  fun updateUserName() {
    val newUserRequest =
      UserCreateRequest(name = "새이름", userId = "새아이디", email = "새이메일", password = "새비밀번호", emailAuthCode = "인증코드")
    val newUser = userService.createUser(
      newUserRequest.name,
      newUserRequest.userId,
      newUserRequest.email,
      newUserRequest.password,
      newUserRequest.emailAuthCode
    )

    val editName = "변경이름"
    val updatedUser = userService.updateUser(newUser.userId, editName, null)


    assertThat(updatedUser.username).isEqualTo(editName)
  }

  @Test
  @DisplayName("유저 이름 업데이트 중복 실패 테스트")
  fun updateFailByDupicateUserName() {
    val newUserRequest =
      UserCreateRequest(name = "새이름", userId = "새아이디", email = "새이메일", password = "새비밀번호", emailAuthCode = "인증코드")
    val newUser = userService.createUser(
      newUserRequest.name,
      newUserRequest.userId,
      newUserRequest.email,
      newUserRequest.password,
      newUserRequest.emailAuthCode
    )

    val editName = "기존이름"
    assertThrows<DataIntegrityViolationException> { userService.updateUser(newUser.userId, editName, null) }
  }

  @Test
  @DisplayName("유저 비밀번호 업데이트 테스트")
  fun updateUser() {
    val newUserRequest =
      UserCreateRequest(name = "새이름", userId = "새아이디", email = "새이메일", password = "새비밀번호", emailAuthCode = "인증코드")
    val newUser = userService.createUser(
      newUserRequest.name,
      newUserRequest.userId,
      newUserRequest.email,
      newUserRequest.password,
      newUserRequest.emailAuthCode
    )

    val editPassword = "변경비밀번호"
    val updatedUser = userService.updateUser(newUser.userId, null, editPassword)
    assertThat(userService.validateUser(newUser.userId, editPassword)).isTrue
  }

  @Test
  @DisplayName("유저 삭제 테스트")
  fun deleteUser() {
    val newUserRequest =
      UserCreateRequest(name = "새이름", userId = "새아이디", email = "새이메일", password = "새비밀번호", emailAuthCode = "인증코드")
    val newUser = userService.createUser(
      newUserRequest.name,
      newUserRequest.userId,
      newUserRequest.email,
      newUserRequest.password,
      newUserRequest.emailAuthCode
    )

    userService.deleteUser(newUser.userId)
    assertThat(userRepository.findByUserId(newUser.userId)).isNull()
  }
}