package gdgoc_otp_team_3.backend.controller

import gdgoc_otp_team_3.backend.dto.*
import gdgoc_otp_team_3.backend.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
  private val userService: UserService,
) {
  @PostMapping("/signup")
  @ResponseStatus(HttpStatus.CREATED)
  fun signup(@RequestBody request: SignupRequest): SignupResponse =
    userService.signup(request)

  @PostMapping("/login")
  fun login(@RequestBody request: LoginRequest): AuthTokenResponse =
    userService.login(request)

  @PostMapping("/email-code")
  fun sendEmailCode(
    @RequestBody request: SendEmailCodeRequest,
  ): SendEmailCodeResponse = userService.sendVerificationCode(request)
}
