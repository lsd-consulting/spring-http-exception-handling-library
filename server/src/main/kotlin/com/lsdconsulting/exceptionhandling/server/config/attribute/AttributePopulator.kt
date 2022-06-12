package com.lsdconsulting.exceptionhandling.server.config.attribute

import com.lsdconsulting.exceptionhandling.server.config.log
import com.lsdconsulting.exceptionhandling.server.tracer.RequestTracer
import org.apache.commons.lang3.StringUtils
import org.springframework.core.NestedRuntimeException
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST
import org.springframework.web.context.request.WebRequest
import java.util.*

@Component
class AttributePopulator(
    private val requestTracer: RequestTracer
) {

    fun populateAttributes(ex: Exception, request: WebRequest): Map<String, Any?> {
        val attributes: MutableMap<String, Any?> = HashMap()
        attributes[TRACE_ID_ATTRIBUTE] = requestTracer.getTraceId()
        populateException(ex, attributes)
        valueOf(
            request.getAttribute(
                "rest.request.receivedAt",
                SCOPE_REQUEST
            )!!
        ).map { value: String ->
            attributes.put(
                START_TIME_ATTRIBUTE, value
            )
        }
        log().error("Populated error response attributes:{}", attributes)
        return attributes
    }

    private fun populateException(ex: Exception, attributes: MutableMap<String, Any?>) {
        if (ex is NestedRuntimeException) {
            val throwable = Optional.ofNullable(ex.rootCause).orElse(ex)
            attributes[EXCEPTION_ATTRIBUTE] = throwable.javaClass.simpleName
        } else {
            attributes[EXCEPTION_ATTRIBUTE] = ex.javaClass.simpleName
        }
    }

    // TODO Refactor to remove Optional
    private fun valueOf(o: Any): Optional<String> {
        return Optional.ofNullable(o)
            .filter { v: Any -> StringUtils.isNotBlank(v.toString()) }
            .map { obj: Any -> obj.toString() }
    }

    companion object {
        private const val TRACE_ID_ATTRIBUTE = "traceId"
        private const val EXCEPTION_ATTRIBUTE = "exception"
        private const val START_TIME_ATTRIBUTE = "startTime"
    }
}
