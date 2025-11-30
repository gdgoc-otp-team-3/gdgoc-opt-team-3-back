package gdgoc_otp_team_3.backend.security

import gdgoc_otp_team_3.backend.security.CurrentUser
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class UserArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(User::class.java) &&
            CurrentUser::class.java.isAssignableFrom(parameter.parameterType)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("Authentication not found in context")
        val principal = authentication.principal
        if (principal is CurrentUser) {
            return principal
        }
        throw IllegalStateException("Unsupported principal type: ${principal?.javaClass?.name}")
    }
}
