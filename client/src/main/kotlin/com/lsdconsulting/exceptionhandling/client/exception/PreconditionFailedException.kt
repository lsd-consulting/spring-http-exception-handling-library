package com.lsdconsulting.exceptionhandling.client.exception

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.springframework.http.HttpStatus.PRECONDITION_FAILED

@JsonDeserialize(using = PreconditionFailedExceptionDeserializer::class)
class PreconditionFailedException : ErrorResponseException {
    constructor(errorResponse: ErrorResponse?) : super(errorResponse, PRECONDITION_FAILED)
    constructor(message: String) : super(message, PRECONDITION_FAILED)
}

class PreconditionFailedExceptionDeserializer : JsonDeserializer<PreconditionFailedException>() {
    override fun deserialize(jp: JsonParser, dc: DeserializationContext): PreconditionFailedException {
        val errorResponse = jp.readValueAs(ErrorResponse::class.java)
        return PreconditionFailedException(errorResponse)
    }
}
