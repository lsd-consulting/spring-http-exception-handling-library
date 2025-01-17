package com.lsdconsulting.exceptionhandling.server.integration.client

import com.fasterxml.jackson.core.JsonProcessingException
import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import com.lsdconsulting.exceptionhandling.api.mapper.ObjectMapperBuilder.objectMapper
import com.lsdconsulting.exceptionhandling.client.exception.*
import com.lsdconsulting.exceptionhandling.server.exension.ResourcesApprovalsExtension
import com.lsdconsulting.exceptionhandling.server.integration.config.IntegrationTestConfiguration
import com.lsdconsulting.exceptionhandling.server.testapp.TestApplication
import com.lsdconsulting.exceptionhandling.server.testapp.client.TestClient
import com.oneeyedmen.okeydoke.Approver
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus.*
import org.springframework.test.context.TestPropertySource
import java.io.IOException
import java.security.SecureRandom
import java.util.stream.Stream

@SpringBootTest(webEnvironment = DEFINED_PORT, classes = [TestApplication::class])
@ExtendWith(ResourcesApprovalsExtension::class)
@TestPropertySource("classpath:application-test.properties")
@EnableFeignClients(clients = [TestClient::class])
@Import(IntegrationTestConfiguration::class)
@AutoConfigureObservability
internal class ClientBasedExceptionHandlingIntegrationShould(
    @Autowired private val testClient: TestClient
) {
    private val objectWriter = objectMapper.writerWithDefaultPrettyPrinter()

    @Test
    @Throws(ErrorResponseException::class)
    internal fun `return object with same object id as requested`() {
        val id = SecureRandom().nextLong(1000L, Long.MAX_VALUE)

        val testResponse = testClient.getObject(id)

        assertThat(testResponse?.message, `is`("message"))
        assertThat(testResponse?.id, `is`(id))
    }

    @Test
    @Throws(IOException::class)
    internal fun `throw internal server exception for runtime exception`(approver: Approver) {
        val resultException = assertThrows(InternalServerException::class.java) { testClient.withTestException }

        assertThat(resultException.httpStatus, `is`(INTERNAL_SERVER_ERROR))
        approver.assertApproved(asString(resultException.errorResponse))
    }

    @Test
    @Throws(IOException::class)
    internal fun `throw internal server exception with data error`(approver: Approver) {
        val value = 9999999L
        val resultException = assertThrows(InternalServerException::class.java) { testClient.getWithExceptionAndCustomParam(value) }

        assertThat(resultException.httpStatus, `is`(INTERNAL_SERVER_ERROR))
        approver.assertApproved(asString(resultException.errorResponse))
    }

    @Test
    @Throws(IOException::class)
    internal fun `throw internal server exception with message for runtime exception`(approver: Approver) {
        val resultException = assertThrows(InternalServerException::class.java) { testClient.withException }

        assertThat(resultException.httpStatus, `is`(INTERNAL_SERVER_ERROR))
        approver.assertApproved(asString(resultException.errorResponse))
    }

    @Test
    @Throws(IOException::class)
    internal fun `throw internal server exception with message for checked exception`(approver: Approver) {
        val resultException = assertThrows(InternalServerException::class.java) { testClient.withCheckedException }

        assertThat(resultException.httpStatus, `is`(INTERNAL_SERVER_ERROR))
        approver.assertApproved(asString(resultException.errorResponse))
    }

    @Test
    @Throws(IOException::class)
    internal fun `throw internal server exception disregarding resource annotation`(approver: Approver) {
        val resultException = assertThrows(InternalServerException::class.java) { testClient.withExceptionAndAnnotatedStatusResource }

        assertThat(resultException.httpStatus, `is`(INTERNAL_SERVER_ERROR))
        approver.assertApproved(asString(resultException.errorResponse))
    }

    @Test
    @Throws(IOException::class)
    internal fun `throw conflict exception from annotated exception status`(approver: Approver) {
        val resultException = assertThrows(ConflictException::class.java) { testClient.withAnnotatedException }

        assertThat(resultException.httpStatus, `is`(CONFLICT))
        approver.assertApproved(asString(resultException.errorResponse))
    }

    @Test
    @Throws(IOException::class)
    internal fun `throw bad request exception for missing servlet request parameter exception`(approver: Approver) {
        val resultException = assertThrows(BadRequestException::class.java) { testClient.getObjectByMessage(null) }

        assertThat(resultException.httpStatus, `is`(BAD_REQUEST))
        approver.assertApproved(asString(resultException.errorResponse))
    }

    @Test
    @Throws(IOException::class)
    internal fun `throw not found exception`(approver: Approver) {
        val resultException = assertThrows(NotFoundException::class.java) { testClient.withNotFoundException }

        assertThat(resultException.httpStatus, `is`(NOT_FOUND))
        approver.assertApproved(asString(resultException.errorResponse))
    }

    @Test
    @Throws(IOException::class)
    internal fun `throw conflict exception`(approver: Approver) {
        val resultException = assertThrows(ConflictException::class.java) { testClient.withConflictException }

        assertThat(resultException.httpStatus, `is`(CONFLICT))
        approver.assertApproved(asString(resultException.errorResponse))
    }

    @Test
    @Throws(IOException::class)
    internal fun `throw precondition failed exception`(approver: Approver) {
        val resultException = assertThrows(PreconditionFailedException::class.java) { testClient.withPreconditionFailedException }

        assertThat(resultException.httpStatus, `is`(PRECONDITION_FAILED))
        approver.assertApproved(asString(resultException.errorResponse))
    }

    @ParameterizedTest
    @MethodSource("provideResponseCodeAndExceptionType")
    internal fun `throw exception corresponding to response code when response malformed`(statusCode: Int, exception: Class<out ErrorResponseException?>?) {
        val resultException = assertThrows(exception) { testClient.getMalformedResponse(statusCode) }

        assertThat(resultException, `is`(notNullValue()))
        assertThat(resultException?.message, `is`("Error message unavailable"))
    }

    @ParameterizedTest
    @MethodSource("provideResponseCodeAndExceptionType")
    internal fun `throw exception corresponding to response code when empty response`(statusCode: Int, exception: Class<out ErrorResponseException?>?) {
        val resultException = assertThrows(exception) { testClient.getEmptyResponse(statusCode) }

        assertThat(resultException, `is`(notNullValue()))
        assertThat(resultException?.message, `is`("Error message unavailable"))
    }

    @Throws(JsonProcessingException::class)
    private fun asString(errorResponse: ErrorResponse) = objectWriter.writeValueAsString(errorResponse)

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
