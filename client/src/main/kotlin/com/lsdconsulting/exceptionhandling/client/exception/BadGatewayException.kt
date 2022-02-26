package com.lsdconsulting.exceptionhandling.client.exception

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.springframework.http.HttpStatus.BAD_GATEWAY

class BadGatewayException : ErrorResponseException {
    constructor(errorResponse: ErrorResponse?) : super(errorResponse, BAD_GATEWAY)
    constructor(message: String) : super(message, BAD_GATEWAY)
}
