package com.lsdconsulting.exceptionhandling.client.exception

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.springframework.http.HttpStatus.GATEWAY_TIMEOUT

@JsonDeserialize(using = GatewayTimeoutExceptionDeserializer::class)
class GatewayTimeoutException : ErrorResponseException {
    constructor(errorResponse: ErrorResponse?) : super(errorResponse, GATEWAY_TIMEOUT)
    constructor(message: String) : super(message, GATEWAY_TIMEOUT)
}

class GatewayTimeoutExceptionDeserializer : JsonDeserializer<GatewayTimeoutException>() {
    override fun deserialize(jp: JsonParser, dc: DeserializationContext): GatewayTimeoutException {
        val errorResponse = jp.readValueAs(ErrorResponse::class.java)
        return GatewayTimeoutException(errorResponse)
    }
}
