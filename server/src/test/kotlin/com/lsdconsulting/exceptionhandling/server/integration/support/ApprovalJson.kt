package com.lsdconsulting.exceptionhandling.server.integration.support

import com.fasterxml.jackson.databind.ObjectWriter
import com.lsdconsulting.exceptionhandling.api.ErrorResponse

/**
 * Brave 6 / Boot 4 emits 128-bit trace IDs that change per request even when a fixed
 * b3 header is configured for some clients. Normalize for stable okeydoke baselines.
 */
object ApprovalJson {
    private const val STABLE_TRACE_ID = "40e1488ed0001adc"

    fun write(objectWriter: ObjectWriter, errorResponse: ErrorResponse): String {
        val attrs = errorResponse.attributes.toMutableMap()
        if (attrs.containsKey("traceId")) {
            attrs["traceId"] = STABLE_TRACE_ID
        }
        return objectWriter.writeValueAsString(errorResponse.copy(attributes = attrs))
    }
}
