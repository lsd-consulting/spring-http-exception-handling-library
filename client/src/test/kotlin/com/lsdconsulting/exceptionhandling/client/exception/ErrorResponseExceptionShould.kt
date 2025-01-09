package com.lsdconsulting.exceptionhandling.client.exception

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus.*
import java.util.stream.Stream

internal class ErrorResponseExceptionShould {

    private val message = randomAlphanumeric(20)

    @Test
    internal fun `preserve exception message through message constructor`() {
        val result = object : ErrorResponseException(message, OK) {}
        assertThat(result.message, `is`(message))
    }

    @Test
    internal fun `preserve exception message through error detail response constructor`() {
        val result = object : ErrorResponseException(ErrorResponse(messages = listOf(message)), OK) {}
        assertThat(result.message, `is`(message))
    }

    @Test
    internal fun `preserve message through message constructor`() {
        val result = object : ErrorResponseException(message, OK) {}
        assertThat(result.errorResponse.messages[0], `is`(message))
    }

    @Test
    internal fun `preserve message through error detail response constructor`() {
        val result = object : ErrorResponseException(ErrorResponse(messages = listOf(message)), OK) {}
        assertThat(result.errorResponse.messages[0], `is`(message))
    }

    @Test
    internal fun `handle empty message through error detail response constructor`() {
        val result = object : ErrorResponseException(ErrorResponse(), OK) {}
        assertThat(result.message, `is`("Error message unavailable"))
    }

    @Test
    internal fun `handle empty error detail response`() {
        val result = object : ErrorResponseException(null as ErrorResponse?, OK) {}
        assertThat(result.message, `is`("Error message unavailable"))
    }

    @Test
    internal fun `should not throw npe while accessing error code when empty error detail response`() {
        val result = object : ErrorResponseException(null as ErrorResponse?, OK) {}
        assertThat(result.errorResponse.errorCode, `is`("UNKNOWN"))
    }

    @ParameterizedTest
    @MethodSource("provide exception type and response body")
    internal fun `should create instance of empty exception`(
        exception: Class<out ErrorResponseException>,
        responseBody: String?,
        statusCode: Int
    ) {
        val result = ErrorResponseException.create(exception = exception, responseBody = responseBody)!!
        assertThat(result, notNullValue())
        assertThat(result.httpStatus, `is`(valueOf(statusCode)))
    }

    companion object {
        @JvmStatic
        private fun `provide exception type and response body`() = Stream.of(
            Arguments.of(BadRequestException::class.java, null, BAD_REQUEST.value()),
            Arguments.of(NotFoundException::class.java, null, NOT_FOUND.value()),
            Arguments.of(ConflictException::class.java, null, CONFLICT.value()),
            Arguments.of(PreconditionFailedException::class.java, null, PRECONDITION_FAILED.value()),
            Arguments.of(InternalServerException::class.java, null, INTERNAL_SERVER_ERROR.value()),
            Arguments.of(NotImplementedException::class.java, null, NOT_IMPLEMENTED.value()),
            Arguments.of(BadGatewayException::class.java, null, BAD_GATEWAY.value()),
            Arguments.of(ServiceUnavailableException::class.java, null, SERVICE_UNAVAILABLE.value()),
            Arguments.of(GatewayTimeoutException::class.java, null, GATEWAY_TIMEOUT.value()),
            Arguments.of(BadRequestException::class.java, "{}", BAD_REQUEST.value()),
            Arguments.of(NotFoundException::class.java, "{}", NOT_FOUND.value()),
            Arguments.of(ConflictException::class.java, "{}", CONFLICT.value()),
            Arguments.of(PreconditionFailedException::class.java, "{}", PRECONDITION_FAILED.value()),
            Arguments.of(InternalServerException::class.java, "{}", INTERNAL_SERVER_ERROR.value()),
            Arguments.of(NotImplementedException::class.java, "{}", NOT_IMPLEMENTED.value()),
            Arguments.of(BadGatewayException::class.java, "{}", BAD_GATEWAY.value()),
            Arguments.of(ServiceUnavailableException::class.java, "{}", SERVICE_UNAVAILABLE.value()),
            Arguments.of(GatewayTimeoutException::class.java, "{}", GATEWAY_TIMEOUT.value())
        )
    }
}
