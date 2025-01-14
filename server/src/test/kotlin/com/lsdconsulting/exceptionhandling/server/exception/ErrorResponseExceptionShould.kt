package com.lsdconsulting.exceptionhandling.server.exception.com.lsdconsulting.exceptionhandling.service.exception

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import com.lsdconsulting.exceptionhandling.server.exception.ErrorResponseException
import org.apache.commons.lang3.RandomStringUtils.secure
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.OK

internal class ErrorResponseExceptionShould {

    private val message = secure().nextAlphanumeric(20)

    @Test
    internal fun `preserve exception message through error detail response constructor`() {
        val result = object : ErrorResponseException(
            errorResponse = ErrorResponse(messages = listOf(message)),
            httpStatus = OK
        ) {}
        assertThat(result.message, `is`(message))
    }

    @Test
    internal fun `preserve message through error detail response constructor`() {
        val result = object : ErrorResponseException(
            errorResponse = ErrorResponse(messages = listOf(message)),
            httpStatus = OK
        ) {}
        assertThat(result.errorResponse.messages[0], `is`(message))
    }

    @Test
    internal fun `handle empty message through error detail response constructor`() {
        val result = object : ErrorResponseException(errorResponse = ErrorResponse(), httpStatus = OK) {}
        assertThat(result.message, `is`("Error message unavailable"))
    }
}
