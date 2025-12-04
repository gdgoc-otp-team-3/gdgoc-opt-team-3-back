package gdgoc_otp_team_3.backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Component
data class AwsProperties(
  @Value("\${aws.region}") val region: String,
  @Value("\${aws.s3.bucket}") val bucket: String,
  @Value("\${aws.s3.presign-expiration-seconds:600}") val presignExpirationSeconds: Long,
)

@Configuration
class AwsConfig(
  private val awsProperties: AwsProperties,
) {
  private fun region(): Region = Region.of(awsProperties.region)

  @Bean
  fun s3Client(): S3Client = S3Client.builder()
    .region(region())
    .credentialsProvider(DefaultCredentialsProvider.create())
    .build()

  @Bean
  fun s3Presigner(): S3Presigner = S3Presigner.builder()
    .region(region())
    .credentialsProvider(DefaultCredentialsProvider.create())
    .build()
}
