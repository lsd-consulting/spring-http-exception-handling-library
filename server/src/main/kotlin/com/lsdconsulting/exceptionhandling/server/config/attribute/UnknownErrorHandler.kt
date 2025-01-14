package com.lsdconsulting.exceptionhandling.server.config.attribute

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import lsd.logging.log
import org.springframework.stereotype.Component
import org.springframework.web.context.request.WebRequest

@Component
class UnknownErrorHandler(private val attributePopulator: AttributePopulator) {

    fun handle(ex: Exception, request: WebRequest): ErrorResponse {
        log().warn("Unknown exception", ex)
        return ErrorResponse(
            errorCode = UNKNOWN_ERROR_CODE,
            messages = if (ex.message.isNullOrBlank()) listOf() else listOf(ex.message!!),
            attributes = attributePopulator.populateAttributes(ex, request)
        )
    }

    companion object {
        private const val UNKNOWN_ERROR_CODE = "UNKNOWN_ERROR"
    }
}
