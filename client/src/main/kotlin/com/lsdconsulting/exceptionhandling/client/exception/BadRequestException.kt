package com.lsdconsulting.exceptionhandling.client.exception

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.springframework.http.HttpStatus.BAD_REQUEST

@JsonDeserialize(using = BadRequestExceptionDeserializer::class)
class BadRequestException : ErrorResponseException {
    constructor(errorResponse: ErrorResponse?) : super(errorResponse, BAD_REQUEST)
    constructor(message: String) : super(message, BAD_REQUEST)
}

class BadRequestExceptionDeserializer : JsonDeserializer<BadRequestException>() {
    override fun deserialize(jp: JsonParser, dc: DeserializationContext): BadRequestException {
        val errorResponse = jp.readValueAs(ErrorResponse::class.java)
        return BadRequestException(errorResponse)
    }
}
