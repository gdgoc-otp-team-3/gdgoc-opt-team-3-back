package gdgoc_otp_team_3.backend.support

import gdgoc_otp_team_3.backend.entity.UserEntity
import gdgoc_otp_team_3.backend.repository.UserRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.crypto.SecretKey

@Component
class AuthSupport(
  private val userRepository: UserRepository,
  @Value("\${jwt.expiration-hours:12}") private val expirationHours: Long,
) {
  private val key: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)

  fun issueToken(user: UserEntity): String {
    val now = Date()
    val expiration = Date(now.time + expirationHours * 60 * 60 * 1000)
    return Jwts.builder()
      .setSubject(user.id?.toString() ?: "")
      .setIssuedAt(now)
      .setExpiration(expiration)
      .signWith(key, SignatureAlgorithm.HS256)
      .compact()
  }

  fun resolveUser(authHeader: String?): UserEntity {
    val tokenValue = extractToken(authHeader)
    val claims = try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(tokenValue).body
    } catch (ex: Exception) {
      throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token")
    }
    val userId = claims.subject.toLongOrNull()
      ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token subject")

    return userRepository.findById(userId).orElseThrow {
      ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found for token")
    }
  }

  fun resolveTokenValue(authHeader: String?): String = extractToken(authHeader)

  private fun extractToken(authHeader: String?): String {
    if (authHeader.isNullOrBlank()) {
      throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is missing")
    }
    val value = authHeader.removePrefix("Bearer").trim()
    if (value.isBlank()) {
      throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token format is invalid")
    }
    return value
  }
}
