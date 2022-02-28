package com.lsdconsulting.exceptionhandling.server.exception.com.lsdconsulting.exceptionhandling.service.exception

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import com.lsdconsulting.exceptionhandling.server.exception.ErrorResponseException
import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.OK

internal class ErrorResponseExceptionShould {

    private val message = randomAlphanumeric(20)

    @Test
    fun preserveExceptionMessageThroughErrorDetailResponseConstructor() {
        val result: ErrorResponseException =
            object : ErrorResponseException(ErrorResponse(messages = listOf(message)), OK) {}
        assertThat(result.message, `is`(message))
    }

    @Test
    fun preserveMessageThroughErrorDetailResponseConstructor() {
        val result: ErrorResponseException =
            object : ErrorResponseException(ErrorResponse(messages = listOf(message)), OK) {}
        assertThat(result.errorResponse.messages[0], `is`(message))
    }

    @Test
    fun handleEmptyMessageThroughErrorDetailResponseConstructor() {
        val result: ErrorResponseException =
            object : ErrorResponseException(ErrorResponse(), OK) {}
        assertThat(result.message, `is`("Error message unavailable"))
    }
}
