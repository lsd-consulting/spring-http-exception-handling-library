package com.lsdconsulting.exceptionhandling.server.config

import com.lsdconsulting.exceptionhandling.server.tracer.RequestTracer
import org.springframework.boot.actuate.autoconfigure.tracing.BraveAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.WebRequest

/*
 This class is only used currently for NOT_FOUND
*/
@Component
@AutoConfigureAfter(BraveAutoConfiguration::class)
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
        valueOf(srcErrorAttributes["path"])?.let { attributes["path"] = it }
        valueOf(srcErrorAttributes["exception"])?.let { attributes["exception"] = it }
        valueOf(webRequest.getAttribute("rest.request.receivedAt", RequestAttributes.SCOPE_REQUEST))?.let { attributes["startTime"] = it }
        (requestTracer.getTraceId()?:parseTraceId(webRequest)).let { attributes["traceId"] = it  }

        // Messages
        val messages = mutableListOf<String>()
        valueOf(srcErrorAttributes["error"])?.let { messages.add(it) }
        valueOf(srcErrorAttributes["message"])?.let { messages.add(it) }

        // Http error code
        val errorCode = HttpStatus.valueOf(statusValueOf(srcErrorAttributes["status"])!!).name

        return mutableMapOf(
            "errorCode" to errorCode,
            "messages" to messages.toList(),
            "attributes" to attributes
        )
    }

    private fun valueOf(o: Any?): String? = o?.toString()?.ifBlank { null }

    private fun statusValueOf(o: Any?): Int? = o?.toString().let { Integer.valueOf(it) }

    private fun parseTraceId(webRequest: WebRequest): String? {
        // TODO: add support for the W3C trace context
        return webRequest.getHeader("b3")?.let { b3: String ->
            b3.split("-".toRegex()).first()
        }
    }
}
