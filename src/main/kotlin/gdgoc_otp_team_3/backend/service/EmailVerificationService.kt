package gdgoc_otp_team_3.backend.service

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.time.Duration
import kotlin.random.Random

@Service
class EmailVerificationService(
    private val redisTemplate: StringRedisTemplate,
    private val mailSender: JavaMailSender,
) {
    private val ttl: Duration = Duration.ofMinutes(5)

    fun sendCode(email: String): String {
        val code = generateCode()
        redisTemplate.opsForValue().set(key(email), code, ttl)
        sendEmail(email, code)
        return code
    }

    fun verifyCode(email: String, code: String): Boolean {
        val stored = redisTemplate.opsForValue().get(key(email))
        return stored != null && stored == code
    }

    private fun sendEmail(email: String, code: String) {
        val message = SimpleMailMessage().apply {
            setTo(email)
            subject = "[RunUs] 이메일 인증코드"
            text = "인증코드: $code (5분 이내 입력해주세요)"
        }
        mailSender.send(message)
    }

    private fun key(email: String) = "verify:$email"

    private fun generateCode(): String = (0..999999).random(Random).toString().padStart(6, '0')
}
