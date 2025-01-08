package com.lsdconsulting.exceptionhandling.server.config.attribute

import com.lsdconsulting.exceptionhandling.server.tracer.RequestTracer
import org.springframework.core.NestedRuntimeException
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST
import org.springframework.web.context.request.WebRequest

@Component
class AttributePopulator(private val requestTracer: RequestTracer) {
    fun populateAttributes(ex: Exception, request: WebRequest): Map<String, Any> {
        val attributes: MutableMap<String, Any> = mutableMapOf()
        requestTracer.getTraceId()?.let { attributes[TRACE_ID_ATTRIBUTE] = it }
        populateException(ex, attributes)
        request.getAttribute("rest.request.receivedAt", SCOPE_REQUEST)?.toString()
            ?.let { attributes[START_TIME_ATTRIBUTE] = it }
        return attributes.toMap()
    }

    private fun populateException(ex: Exception, attributes: MutableMap<String, Any>) {
        attributes[EXCEPTION_ATTRIBUTE] =
            if (ex is NestedRuntimeException) {
                val throwable = ex.rootCause ?: ex
                throwable.javaClass.simpleName
            } else {
                ex.javaClass.simpleName
            }
    }

    companion object {
        private const val TRACE_ID_ATTRIBUTE = "traceId"
        private const val EXCEPTION_ATTRIBUTE = "exception"
        private const val START_TIME_ATTRIBUTE = "startTime"
    }
}
