package com.lsdconsulting.exceptionhandling.server.config.attribute

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import com.lsdconsulting.exceptionhandling.server.config.log
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import org.springframework.web.context.request.WebRequest

@Component
class UnknownErrorHandler(
    private val attributePopulator: AttributePopulator
) {
    fun handle(ex: Exception, request: WebRequest?): ErrorResponse {
        log().warn("Unknown exception", ex)
        return ErrorResponse(
            messages = if (ex.message != null && !StringUtils.isBlank(ex.message)) listOf(ex.message!!) else listOf(),
            errorCode = UNKNOWN_ERROR_CODE,
            attributes = attributePopulator.populateAttributes(ex, request!!)
        )
    }

    companion object {
        private const val UNKNOWN_ERROR_CODE = "UNKNOWN_ERROR"
    }
}
