package gdgoc_otp_team_3.backend.support

import gdgoc_otp_team_3.backend.entity.UserEntity
import gdgoc_otp_team_3.backend.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

private const val TOKEN_PREFIX = "runus-token-"

@Component
class AuthSupport(
    private val userRepository: UserRepository,
) {
    fun issueToken(user: UserEntity): String = "$TOKEN_PREFIX${user.id}"

    fun resolveUser(authHeader: String?): UserEntity {
        val tokenValue = extractToken(authHeader)
        val userId = tokenValue.removePrefix(TOKEN_PREFIX).toLongOrNull()
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token")

        return userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found for token")
        }
    }

    fun resolveTokenValue(authHeader: String?): String {
        return extractToken(authHeader)
    }

    private fun extractToken(authHeader: String?): String {
        if (authHeader.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is missing")
        }
        val value = authHeader.removePrefix("Bearer").trim()
        if (!value.startsWith(TOKEN_PREFIX)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token format is invalid")
        }
        return value
    }
}
