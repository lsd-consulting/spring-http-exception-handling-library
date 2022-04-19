package com.lsdconsulting.exceptionhandling.client.exception

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.springframework.http.HttpStatus.CONFLICT

@JsonDeserialize(using = ConflictExceptionDeserializer::class)
class ConflictException : ErrorResponseException {
    constructor(errorResponse: ErrorResponse?) : super(errorResponse, CONFLICT)
    constructor(message: String) : super(message, CONFLICT)
}

class ConflictExceptionDeserializer : JsonDeserializer<ConflictException>() {
    override fun deserialize(jp: JsonParser, dc: DeserializationContext): ConflictException {
        val errorResponse = jp.readValueAs(ErrorResponse::class.java)
        return ConflictException(errorResponse)
    }
}
