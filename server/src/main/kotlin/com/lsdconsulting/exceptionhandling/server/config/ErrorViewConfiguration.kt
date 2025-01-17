package com.lsdconsulting.exceptionhandling.server.config

import com.lsdconsulting.exceptionhandling.api.mapper.ObjectMapperBuilder.objectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.autoconfigure.condition.*
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProviders
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ConditionContext
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.core.type.AnnotatedTypeMetadata
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE
import org.springframework.lang.Nullable
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.View

@Configuration
class ErrorViewConfiguration {

    @Suppress("unused")
    @ConditionalOnProperty(prefix = "server.error.whitelabel", name = ["enabled"], matchIfMissing = true)
    @Conditional(ErrorTemplateMissingCondition::class)
    class CustomErrorViewConfiguration {

        @Bean(name = ["error"])
        @ConditionalOnMissingBean(name = ["error"])
        fun customErrorView() = object : View {

            override fun render(@Nullable model: MutableMap<String, *>?, request: HttpServletRequest, response: HttpServletResponse) {
                if (response.contentType == null) {
                    response.contentType = contentType
                }
                response.writer.append(objectMapper.writeValueAsString(model))
            }

            override fun getContentType() = APPLICATION_PROBLEM_JSON_VALUE
        }

        @Bean
        fun customErrorViewResolver(): ErrorViewResolver =
            ErrorViewResolver { _: HttpServletRequest, _: HttpStatus, model: Map<String, Any> ->
                ModelAndView(customErrorView(), model)
            }
    }

    // See ErrorMvcAutoConfiguration$ErrorTemplateMissingCondition
    private class ErrorTemplateMissingCondition : SpringBootCondition() {
        override fun getMatchOutcome(context: ConditionContext, metadata: AnnotatedTypeMetadata): ConditionOutcome {
            val message = ConditionMessage.forCondition("ErrorTemplate Missing")
            val providers = TemplateAvailabilityProviders(context.classLoader)
            val provider = providers.getProvider("error", context.environment, context.classLoader, context.resourceLoader)
            return if (provider != null) {
                ConditionOutcome.noMatch(message.foundExactly("template from $provider"))
            } else {
                ConditionOutcome.match(message.didNotFind("error template view").atAll())
            }
        }
    }
}
