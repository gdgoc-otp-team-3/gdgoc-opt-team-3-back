package gdgoc_otp_team_3.backend.service

import org.springframework.cache.CacheManager
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class EmailVerificationService(
  private val cacheManager: CacheManager,
  private val mailSender: JavaMailSender,
) {
  private val cacheName = "emailCodes"

  fun sendCode(email: String): String {
    val code = generateCode()
    cache().put(email, code)
    sendEmail(email, code)
    return code
  }

  fun verifyCode(email: String, code: String): Boolean {
    val stored = cache().get(email, String::class.java) ?: return false
    return stored == code
  }

  private fun sendEmail(email: String, code: String) {
    val message = SimpleMailMessage().apply {
      setTo(email)
      subject = "[BLUE NOTE] 이메일 인증코드"
      text = "인증코드: $code (5분 이내 입력해주세요)"
    }
    mailSender.send(message)
  }

  private fun cache() = cacheManager.getCache(cacheName)
    ?: throw IllegalStateException("Cache '$cacheName' not configured")

  private fun generateCode(): String = (0..999999).random(Random).toString().padStart(6, '0')
}
