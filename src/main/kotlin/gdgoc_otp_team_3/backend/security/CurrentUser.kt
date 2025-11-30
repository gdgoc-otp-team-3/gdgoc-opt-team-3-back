package gdgoc_otp_team_3.backend.security

data class CurrentUser(
    val id: Long,
    val email: String,
    val username: String,
)
