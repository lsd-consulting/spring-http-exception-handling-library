package com.lsdconsulting.exceptionhandling.client.exception

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import com.lsdconsulting.exceptionhandling.client.config.ErrorResponseFactory.from
import lsd.logging.log
import org.springframework.http.HttpStatus

abstract class ErrorResponseException(errorResponse: ErrorResponse?, val httpStatus: HttpStatus) :
    Exception(findMessage(errorResponse)) {

    val errorResponse: ErrorResponse = errorResponse ?: DEFAULT_ERROR_DETAIL_RESPONSE

    protected constructor(message: String, httpStatus: HttpStatus) : this(
        ErrorResponse(
            errorCode = DEFAULT_ERROR_CODE,
            messages = listOf(message),
            dataErrors = listOf(),
            attributes = mapOf()
        ),
        httpStatus
    )

    companion object {
        private const val DEFAULT_ERROR_MESSAGE = "Error message unavailable"
        private const val DEFAULT_ERROR_CODE = "UNKNOWN"
        private val DEFAULT_ERROR_DETAIL_RESPONSE = ErrorResponse(
            errorCode = DEFAULT_ERROR_CODE,
            messages = listOf(),
            dataErrors = listOf(),
            attributes = mapOf()
        )
        private fun findMessage(errorResponse: ErrorResponse?) =
            errorResponse?.messages?.firstOrNull() ?: DEFAULT_ERROR_MESSAGE

        fun <T> create(exception: Class<T>, responseBody: String?): T? {
            return try {
                val errorResponse: ErrorResponse = try {
                    from(json = responseBody!!)
                } catch (e: Exception) {
                    log().error(e.message)
                    return exception.getConstructor(ErrorResponse::class.java).newInstance(DEFAULT_ERROR_DETAIL_RESPONSE)
                }
                exception.getConstructor(ErrorResponse::class.java).newInstance(errorResponse)
            } catch (ex: ReflectiveOperationException) {
                log().error(ex.message)
                null
            }
        }
    }
}
