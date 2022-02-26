package com.lsdconsulting.exceptionhandling.client.exception

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.springframework.http.HttpStatus.CONFLICT

class ConflictException : ErrorResponseException {
    constructor(errorResponse: ErrorResponse?) : super(errorResponse, CONFLICT)
    constructor(message: String) : super(message, CONFLICT)
}
