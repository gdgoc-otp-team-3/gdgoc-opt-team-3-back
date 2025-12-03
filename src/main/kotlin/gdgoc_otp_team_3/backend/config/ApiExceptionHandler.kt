package gdgoc_otp_team_3.backend.config

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class ApiExceptionHandler {

  @ExceptionHandler(ResponseStatusException::class)
  fun handleResponseStatus(ex: ResponseStatusException): ResponseEntity<Map<String, String?>> {
    val body = mapOf("message" to (ex.reason ?: "error"))
    return ResponseEntity.status(ex.statusCode).body(body)
  }
}
