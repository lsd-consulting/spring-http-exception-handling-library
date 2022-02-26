package com.lsdconsulting.exceptionhandling.client.exception

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE

class ServiceUnavailableException : ErrorResponseException {
    constructor(errorResponse: ErrorResponse?) : super(errorResponse, SERVICE_UNAVAILABLE)
    constructor(message: String) : super(message, SERVICE_UNAVAILABLE)
}
