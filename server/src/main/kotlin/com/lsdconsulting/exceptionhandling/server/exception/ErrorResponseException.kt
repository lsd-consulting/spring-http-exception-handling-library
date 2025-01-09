package com.lsdconsulting.exceptionhandling.server.exception

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.springframework.http.HttpStatus

// TODO Add a couple of common implementations, e.g. Duplicate or Db related
// TODO Remove the word `exception` from the names of all exceptions
/*
  This class should have the same fields as the corresponding one in the client module.
 */
abstract class ErrorResponseException(val errorResponse: ErrorResponse, val httpStatus: HttpStatus) :
    RuntimeException(errorResponse.messages.firstOrNull() ?: ERROR_MESSAGE) {

    companion object {
        private const val ERROR_MESSAGE = "Error message unavailable"
    }
}
