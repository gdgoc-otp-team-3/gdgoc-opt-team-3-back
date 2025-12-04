package gdgoc_otp_team_3.backend.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

  @Bean
  fun openAPI(): OpenAPI {
    val securitySchemeName = "bearerAuth"

    val bearerScheme = SecurityScheme()
      .type(SecurityScheme.Type.HTTP)
      .scheme("bearer")   // Authorization: Bearer xxx
      .bearerFormat("JWT") // 문서용. 있어도 되고 없어도 됨

    val components = Components()
      .addSecuritySchemes(securitySchemeName, bearerScheme)

    val securityItem = SecurityRequirement()
      .addList(securitySchemeName)

    return OpenAPI()
      .components(components)
      .security(listOf(securityItem))   // == addSecurityItem(...)
  }
}