package gdgoc_otp_team_3.backend.security

import gdgoc_otp_team_3.backend.support.AuthSupport
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
  private val authSupport: AuthSupport,
) : OncePerRequestFilter() {

  override fun shouldNotFilter(request: HttpServletRequest): Boolean {
    val path = request.requestURI
    return path.startsWith("/api/v1/auth/")
  }

  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain,
  ) {
    // Allow downstream services to use @Transactional with repository reads after this point
    try {
      val user = authSupport.resolveUser(request.getHeader("Authorization"))
      val principal = CurrentUser(
        id = user.id ?: -1,
        email = user.email,
        username = user.username,
      )
      val authentication = UsernamePasswordAuthenticationToken(
        principal,
        null,
        emptyList(),
      ).apply {
        details = WebAuthenticationDetailsSource().buildDetails(request)
      }
      SecurityContextHolder.getContext().authentication = authentication
    } catch (ex: Exception) {
      response.status = HttpStatus.UNAUTHORIZED.value()
      response.writer.write("Unauthorized: ${ex.message}")
      return
    }
    filterChain.doFilter(request, response)
  }
}
