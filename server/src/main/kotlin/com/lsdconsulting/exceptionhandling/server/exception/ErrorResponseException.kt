package com.lsdconsulting.exceptionhandling.server.exception

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.util.CollectionUtils
import java.util.*

/*
  This class should have the same fields as the corresponding one in the client module.
 */
abstract class ErrorResponseException(val errorResponse: ErrorResponse, val httpStatus: HttpStatus) :
    RuntimeException(
        findMessage(errorResponse)
    ) {

    companion object {
        private const val ERROR_MESSAGE = "Error message unavailable"
        private fun findMessage(errorResponse: ErrorResponse): String {
            return if (Objects.isNull(errorResponse) || CollectionUtils.isEmpty(errorResponse.messages)) ERROR_MESSAGE else errorResponse.messages[0]
        }
    }
}
