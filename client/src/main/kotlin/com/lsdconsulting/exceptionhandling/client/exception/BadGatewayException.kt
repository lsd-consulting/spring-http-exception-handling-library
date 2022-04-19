package com.lsdconsulting.exceptionhandling.client.exception

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.springframework.http.HttpStatus.BAD_GATEWAY

@JsonDeserialize(using = BadGatewayExceptionDeserializer::class)
class BadGatewayException : ErrorResponseException {
    constructor(errorResponse: ErrorResponse?) : super(errorResponse, BAD_GATEWAY)
    constructor(message: String) : super(message, BAD_GATEWAY)
}

class BadGatewayExceptionDeserializer : JsonDeserializer<BadGatewayException>() {
    override fun deserialize(jp: JsonParser, dc: DeserializationContext): BadGatewayException {
        val errorResponse = jp.readValueAs(ErrorResponse::class.java)
        return BadGatewayException(errorResponse)
    }
}
