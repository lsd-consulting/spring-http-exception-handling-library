package com.lsdconsulting.exceptionhandling.client.exception

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

class InternalServerException : ErrorResponseException {
    constructor(errorResponse: ErrorResponse?) : super(errorResponse, INTERNAL_SERVER_ERROR)
    constructor(message: String) : super(message, INTERNAL_SERVER_ERROR)
}
