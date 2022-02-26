package com.lsdconsulting.exceptionhandling.client.exception

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.springframework.http.HttpStatus.NOT_IMPLEMENTED

class NotImplementedException : ErrorResponseException {
    constructor(errorResponse: ErrorResponse?) : super(errorResponse, NOT_IMPLEMENTED)
    constructor(message: String) : super(message, NOT_IMPLEMENTED)
}
