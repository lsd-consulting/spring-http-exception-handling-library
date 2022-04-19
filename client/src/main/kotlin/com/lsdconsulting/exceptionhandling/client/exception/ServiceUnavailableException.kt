package com.lsdconsulting.exceptionhandling.client.exception

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE

@JsonDeserialize(using = ServiceUnavailableExceptionDeserializer::class)
class ServiceUnavailableException : ErrorResponseException {
    constructor(errorResponse: ErrorResponse?) : super(errorResponse, SERVICE_UNAVAILABLE)
    constructor(message: String) : super(message, SERVICE_UNAVAILABLE)
}

class ServiceUnavailableExceptionDeserializer : JsonDeserializer<ServiceUnavailableException>() {
    override fun deserialize(jp: JsonParser, dc: DeserializationContext): ServiceUnavailableException {
        val errorResponse = jp.readValueAs(ErrorResponse::class.java)
        return ServiceUnavailableException(errorResponse)
    }
}
