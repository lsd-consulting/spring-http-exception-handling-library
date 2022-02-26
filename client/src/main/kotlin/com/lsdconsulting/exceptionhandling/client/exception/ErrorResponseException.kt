package com.lsdconsulting.exceptionhandling.client.exception

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import com.lsdconsulting.exceptionhandling.client.config.ErrorResponseFactory.from
import com.lsdconsulting.exceptionhandling.client.config.log
import org.springframework.http.HttpStatus
import org.springframework.util.CollectionUtils
import java.util.*

abstract class ErrorResponseException(errorResponse: ErrorResponse?, val httpStatus: HttpStatus) :
    Exception(findMessage(errorResponse)) {

    val errorResponse: ErrorResponse

    constructor(message: String, httpStatus: HttpStatus) : this(
        ErrorResponse(messages = listOf(message)),
        httpStatus
    )

    companion object {
        private const val ERROR_MESSAGE = "Error message unavailable"
        private const val DEFAULT_ERROR_CODE = "UNKNOWN"
        private val DEFAULT_ERROR_DETAIL_RESPONSE = ErrorResponse(errorCode = DEFAULT_ERROR_CODE)
        private fun findMessage(errorResponse: ErrorResponse?) =
            if (Objects.isNull(errorResponse) || CollectionUtils.isEmpty(
                    errorResponse!!.messages
                )
            ) ERROR_MESSAGE else errorResponse.messages[0]

        fun <T> create(exception: Class<T>, responseBody: String?): T? {
            return try {
                val errorResponse: ErrorResponse = try {
                    from(responseBody!!)
                } catch (e: Exception) {
                    log().error(e.message)
                    return exception.getConstructor(ErrorResponse::class.java).newInstance(ErrorResponse())
                }
                exception.getConstructor(ErrorResponse::class.java).newInstance(errorResponse)
            } catch (ex: ReflectiveOperationException) {
                log().error(ex.message)
                null
            }
        }
    }

    init {
        this.errorResponse = errorResponse ?: DEFAULT_ERROR_DETAIL_RESPONSE
    }
}
