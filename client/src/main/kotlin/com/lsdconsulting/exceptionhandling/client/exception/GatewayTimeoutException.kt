package com.lsdconsulting.exceptionhandling.client.exception

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.springframework.http.HttpStatus.GATEWAY_TIMEOUT

class GatewayTimeoutException : ErrorResponseException {
    constructor(errorResponse: ErrorResponse?) : super(errorResponse, GATEWAY_TIMEOUT)
    constructor(message: String) : super(message, GATEWAY_TIMEOUT)
}
