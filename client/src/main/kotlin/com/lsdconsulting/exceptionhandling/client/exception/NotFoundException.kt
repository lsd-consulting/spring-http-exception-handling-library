package com.lsdconsulting.exceptionhandling.client.exception

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.springframework.http.HttpStatus.NOT_FOUND

class NotFoundException : ErrorResponseException {
    constructor(errorResponse: ErrorResponse?) : super(errorResponse, NOT_FOUND)
    constructor(message: String) : super(message, NOT_FOUND)
}
