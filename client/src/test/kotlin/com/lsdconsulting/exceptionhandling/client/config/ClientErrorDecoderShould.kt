package com.lsdconsulting.exceptionhandling.client.config

import com.lsdconsulting.exceptionhandling.client.exception.*
import feign.Request
import feign.Request.HttpMethod.GET
import feign.RequestTemplate
import feign.Response
import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus.*
import java.nio.charset.Charset.defaultCharset
import java.util.stream.Stream

internal class ClientErrorDecoderShould {

    private val underTest = ClientErrorDecoder()

    @ParameterizedTest
    @MethodSource("provideResponseCodeAndExceptionType")
    internal fun `handle responses with no body`(statusCode: Int, exception: Class<out ErrorResponseException?>?) {
        val response = Response.builder()
            .status(statusCode)
            .request(dummyRequest)
            .build()

        val result = underTest.decode(methodKey = randomAlphanumeric(5), response = response)

        assertThat(result, instanceOf(exception))
    }

    @ParameterizedTest
    @MethodSource("provideResponseCodeAndExceptionType")
    internal fun `handle responses with malformed body`(statusCode: Int, exception: Class<out ErrorResponseException?>?) {
        val response = Response.builder()
            .status(statusCode)
            .body(randomAlphanumeric(10), defaultCharset())
            .request(dummyRequest)
            .build()

        val result = underTest.decode(methodKey = randomAlphanumeric(5), response = response)

        assertThat(result, instanceOf(exception))
    }

    @Test
    internal fun `handle responses with unknown status`() {
        val response = Response.builder()
            .status(999)
            .request(dummyRequest)
            .build()

        val result = underTest.decode(methodKey = randomAlphanumeric(5), response = response)

        assertThat(result, instanceOf(InternalServerException::class.java))
    }

    private val dummyRequest: Request
        get() = Request.create(GET, randomAlphanumeric(10), mapOf(), null, defaultCharset(), RequestTemplate())

    companion object {
        @JvmStatic
        private fun provideResponseCodeAndExceptionType() = Stream.of(
            Arguments.of(BAD_REQUEST.value(), BadRequestException::class.java),
            Arguments.of(NOT_FOUND.value(), NotFoundException::class.java),
            Arguments.of(CONFLICT.value(), ConflictException::class.java),
            Arguments.of(PRECONDITION_FAILED.value(), PreconditionFailedException::class.java),
            Arguments.of(INTERNAL_SERVER_ERROR.value(), InternalServerException::class.java),
            Arguments.of(NOT_IMPLEMENTED.value(), NotImplementedException::class.java),
            Arguments.of(BAD_GATEWAY.value(), BadGatewayException::class.java),
            Arguments.of(SERVICE_UNAVAILABLE.value(), ServiceUnavailableException::class.java),
            Arguments.of(GATEWAY_TIMEOUT.value(), GatewayTimeoutException::class.java)
        )
    }
}
