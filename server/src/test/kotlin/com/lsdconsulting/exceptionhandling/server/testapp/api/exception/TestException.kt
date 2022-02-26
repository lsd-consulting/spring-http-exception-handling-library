package com.lsdconsulting.exceptionhandling.server.testapp.api.exception

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import com.lsdconsulting.exceptionhandling.server.exception.ErrorResponseException
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

class TestException : ErrorResponseException(
    ErrorResponse(messages = listOf("Exception message"), errorCode = "ERROR_CODE"), INTERNAL_SERVER_ERROR
)
