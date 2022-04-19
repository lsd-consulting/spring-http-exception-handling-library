package com.lsdconsulting.exceptionhandling.client.exception

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.springframework.http.HttpStatus.NOT_IMPLEMENTED

@JsonDeserialize(using = NotImplementedExceptionDeserializer::class)
class NotImplementedException : ErrorResponseException {
    constructor(errorResponse: ErrorResponse?) : super(errorResponse, NOT_IMPLEMENTED)
    constructor(message: String) : super(message, NOT_IMPLEMENTED)
}

class NotImplementedExceptionDeserializer : JsonDeserializer<NotImplementedException>() {
    override fun deserialize(jp: JsonParser, dc: DeserializationContext): NotImplementedException {
        val errorResponse = jp.readValueAs(ErrorResponse::class.java)
        return NotImplementedException(errorResponse)
    }
}
