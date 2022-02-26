package com.lsdconsulting.exceptionhandling.server.testapp.api.exception

import com.lsdconsulting.exceptionhandling.api.DataError
import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import com.lsdconsulting.exceptionhandling.server.exception.ErrorResponseException
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

class TestParameterException(private val valueName: String, val value: Long) : ErrorResponseException(
    ErrorResponse(
        messages = listOf("Exception message"),
        errorCode = "SOME_ERROR_CODE",
        dataErrors = listOf(
            DataError(
                code = "Conflict",
                name = valueName,
                message = "exception.conflict.alreadyExists",
                value = value.toString())
        )), INTERNAL_SERVER_ERROR
)
