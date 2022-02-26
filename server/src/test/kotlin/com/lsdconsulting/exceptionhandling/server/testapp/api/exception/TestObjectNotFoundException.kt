package com.lsdconsulting.exceptionhandling.server.testapp.api.exception

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import com.lsdconsulting.exceptionhandling.server.exception.ErrorResponseException
import org.springframework.http.HttpStatus.NOT_FOUND

class TestObjectNotFoundException : ErrorResponseException(
    ErrorResponse(messages = listOf("Object not found"), errorCode = "NOT_FOUND"), NOT_FOUND
)
