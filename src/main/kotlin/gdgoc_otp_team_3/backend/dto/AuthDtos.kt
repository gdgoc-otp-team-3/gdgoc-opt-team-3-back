package gdgoc_otp_team_3.backend.dto

data class SignupRequest(
    val email: String,
    val password: String,
    val username: String,
    val code: String,
)

data class SignupResponse(
    val message: String,
    val userId: Long,
)

data class LoginRequest(
    val email: String,
    val password: String,
)

data class AuthTokenResponse(
    val token: String,
    val user: AuthUser,
)

data class AuthUser(
    val id: Long,
    val email: String,
    val username: String,
)

data class VerifyStudentRequest(
    val email: String,
    val code: String,
)

data class VerifyStudentResponse(
    val message: String,
)

data class SendEmailCodeRequest(
    val email: String,
)

data class SendEmailCodeResponse(
    val message: String,
)
