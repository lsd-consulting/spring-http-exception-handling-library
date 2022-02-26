package com.lsdconsulting.exceptionhandling.client.exception

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.springframework.http.HttpStatus.PRECONDITION_FAILED

class PreconditionFailedException : ErrorResponseException {
    constructor(errorResponse: ErrorResponse?) : super(errorResponse, PRECONDITION_FAILED)
    constructor(message: String) : super(message, PRECONDITION_FAILED)
}
