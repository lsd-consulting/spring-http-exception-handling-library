package com.lsdconsulting.exceptionhandling.client.exception

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.springframework.http.HttpStatus.BAD_REQUEST

class BadRequestException : ErrorResponseException {
    constructor(errorResponse: ErrorResponse?) : super(errorResponse, BAD_REQUEST)
    constructor(message: String) : super(message, BAD_REQUEST)
}
