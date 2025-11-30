package gdgoc_otp_team_3.backend.service

import gdgoc_otp_team_3.backend.dto.*
import gdgoc_otp_team_3.backend.entity.UserEntity
import gdgoc_otp_team_3.backend.repository.UserRepository
import gdgoc_otp_team_3.backend.support.AuthSupport
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
  private val userRepository: UserRepository,
  private val emailVerificationService: EmailVerificationService,
  private val passwordEncoder: PasswordEncoder,
  private val authSupport: AuthSupport,
) {
  @Transactional
  fun signup(request: SignupRequest): SignupResponse {
    validateYonseiEmail(request.email)
    if (userRepository.existsByEmail(request.email)) {
      throw ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.")
    }
    if (userRepository.existsByUsername(request.username)) {
      throw ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 사용자명이 있습니다.")
    }
    val isValid = emailVerificationService.verifyCode(request.email, request.code)
    if (!isValid) {
      throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 코드가 올바르지 않거나 만료되었습니다.")
    }
    val user = userRepository.save(
      UserEntity(
        email = request.email,
        username = request.username,
        password = passwordEncoder.encode(request.password),
      ),
    )
    return SignupResponse(message = "회원가입이 완료되었습니다.", userId = user.id ?: -1)
  }

  @Transactional(readOnly = true)
  fun login(request: LoginRequest): AuthTokenResponse {
    val user = userRepository.findByEmail(request.email)
      ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호를 확인해주세요.")
    if (!passwordEncoder.matches(request.password, user.password)) {
      throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호를 확인해주세요.")
    }
    val token = authSupport.issueToken(user)
    return AuthTokenResponse(
      token = token,
      user = AuthUser(
        id = user.id ?: -1,
        email = user.email,
        username = user.username,
      ),
    )
  }

  @Transactional(readOnly = true)
  fun sendVerificationCode(request: SendEmailCodeRequest): SendEmailCodeResponse {
    validateYonseiEmail(request.email)
    val code = emailVerificationService.sendCode(request.email)
    return SendEmailCodeResponse(message = "인증 코드가 발송되었습니다.")
  }

  fun getUser(userId: Long): UserEntity? = userRepository.findById(userId).orElse(null)

  private fun validateYonseiEmail(email: String) {
    if (!email.endsWith("@yonsei.ac.kr", ignoreCase = true)) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, "연세대 이메일(@yonsei.ac.kr)만 인증할 수 있습니다.")
    }
  }

}
