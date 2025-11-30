package gdgoc_otp_team_3.backend.repository

interface TopContributorProjection {
    fun getUserId(): Long
    fun getUsername(): String
    fun getUploadCount(): Long
}
