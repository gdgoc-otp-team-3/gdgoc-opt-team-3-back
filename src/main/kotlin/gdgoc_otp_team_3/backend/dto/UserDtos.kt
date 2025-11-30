package gdgoc_otp_team_3.backend.dto

data class TopContributorResponse(
  val userId: Long,
  val username: String,
  val uploadCount: Int,
  val rank: Int,
)
