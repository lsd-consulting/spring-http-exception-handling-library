package com.lsdconsulting.exceptionhandling.server.config

import com.lsdconsulting.exceptionhandling.server.tracer.RequestTracer
import org.apache.commons.lang3.StringUtils
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.WebRequest
import java.util.*

/*
 This class is only used currently for NOT_FOUND
*/
@Component
class ErrorAttributesHandler(
    private val requestTracer: RequestTracer
) : DefaultErrorAttributes() {

    override fun getErrorAttributes(
        webRequest: WebRequest,
        errorAttributeOptions: ErrorAttributeOptions
    ): Map<String, Any> {

        val srcErrorAttributes = super.getErrorAttributes(webRequest, errorAttributeOptions)

        // Attributes
        val attributes = mutableMapOf<String, Any?>()
        valueOf(srcErrorAttributes["path"]).map { value: String -> attributes["path"] = value }
        valueOf(srcErrorAttributes["exception"]).map { value: String -> attributes["exception"] = value }
        valueOf(
            webRequest.getAttribute(
                "rest.request.receivedAt",
                RequestAttributes.SCOPE_REQUEST
            )
        ).map { value: String -> attributes["startTime"] = value }
        attributes["traceId"] = requestTracer.getTraceId()

        // Messages
        val messages = mutableListOf<String>()
        valueOf(srcErrorAttributes["error"]).map { message: String -> messages.add(message) }
        valueOf(srcErrorAttributes["message"]).map { message: String -> messages.add(message) }

        // Http error code
        val errorCode = HttpStatus.valueOf(statusValueOf(srcErrorAttributes["status"])!!).name

        return mutableMapOf(
            "errorCode" to errorCode,
            "messages" to messages.toList(),
            "attributes" to attributes
        )
    }

    // TODO Refactor to remove Optional
    private fun valueOf(o: Any?): Optional<String> {
        return Optional.ofNullable(o)
            .filter { v: Any -> StringUtils.isNotBlank(v.toString()) }
            .map { obj: Any -> obj.toString() }
    }

    private fun statusValueOf(o: Any?): Int? = o?.toString().let { Integer.valueOf(it) }
}
