package com.lsdconsulting.exceptionhandling.server.integration.exception

import com.fasterxml.jackson.core.JsonProcessingException
import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import com.lsdconsulting.exceptionhandling.api.mapper.ObjectMapperBuilder.objectMapper
import com.lsdconsulting.exceptionhandling.server.exension.ResourcesApprovalsExtension
import com.lsdconsulting.exceptionhandling.server.integration.config.IntegrationTestConfiguration
import com.lsdconsulting.exceptionhandling.server.testapp.TestApplication
import com.lsdconsulting.exceptionhandling.server.testapp.api.request.TestRequest
import com.lsdconsulting.exceptionhandling.server.testapp.client.TestClient
import com.oneeyedmen.okeydoke.Approver
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.test.context.TestPropertySource
import java.io.IOException

@Import(IntegrationTestConfiguration::class)
@SpringBootTest(webEnvironment = DEFINED_PORT, classes = [TestApplication::class])
@ExtendWith(ResourcesApprovalsExtension::class)
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureObservability
@EnableFeignClients(clients = [TestClient::class])
internal class ExceptionIntegrationShould(
    @Autowired private val testRestTemplate: TestRestTemplate
) {

    private val objectWriter = objectMapper.writerWithDefaultPrettyPrinter()

    @Test
    @Throws(IOException::class)
    internal fun `return500 test exception`(approver: Approver) {
        testRestTemplate.getForEntity("/objects/generateTestException", String::class.java)
        val responseEntity = testRestTemplate.getForEntity("/objects/generateTestException", ErrorResponse::class.java)
        assertThat(responseEntity.statusCode, `is`(INTERNAL_SERVER_ERROR))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    internal fun `return500 test exception with param`(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity(
            "/objects/generateTestExceptionWithCustomParam?someId=99999999",
            ErrorResponse::class.java
        )

        assertThat(responseEntity.statusCode, `is`(INTERNAL_SERVER_ERROR))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    internal fun `return500 exception`(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/objects/generateException", ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(INTERNAL_SERVER_ERROR))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    internal fun `return500 exception disregarding response status annotated resource`(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity(
            "/objects/generateExceptionWithAnnotatedStatusResource",
            ErrorResponse::class.java
        )

        assertThat(responseEntity.statusCode, `is`(INTERNAL_SERVER_ERROR))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    internal fun `return404 test object not found exception`(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/objects/objectNotFoundException", ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(NOT_FOUND))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    internal fun `return405 for http request method not supported exception`(approver: Approver) {
        val responseEntity = testRestTemplate.postForEntity("/objects/1", TestRequest(), ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(METHOD_NOT_ALLOWED))
        approver.assertApproved(asString(responseEntity))
    }

    @Test
    @Throws(IOException::class)
    internal fun `return409 annotated status on exception`(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/objects/generateAnnotatedException", ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(CONFLICT))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    internal fun `return409 annotated status on exception with message`(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/objects/generateAnnotatedExceptionWithMessage", ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(CONFLICT))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    internal fun `return 409 test duplicate exception`(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/objects/conflict", ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(CONFLICT))
        assertThat(responseEntity.body, `is`(notNullValue()))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    internal fun `return 500 test database response exception`(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/objects/internalServerError", ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(INTERNAL_SERVER_ERROR))
        assertThat(responseEntity.body, `is`(notNullValue()))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    internal fun `return507 response status exception with message`(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/objects/generateResponseStatusException", ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(INSUFFICIENT_STORAGE))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    internal fun `return507 response status exception without message`(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/objects/generateResponseStatusExceptionNoMessage", ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(INSUFFICIENT_STORAGE))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    internal fun `return507 annotated response status exception`(approver: Approver) {
        testRestTemplate.getForEntity("/objects/generateAnnotatedResponseStatusException", String::class.java)
        val responseEntity = testRestTemplate.getForEntity("/objects/generateAnnotatedResponseStatusException", ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(INSUFFICIENT_STORAGE))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Throws(JsonProcessingException::class)
    private fun asString(errorResponse: ErrorResponse) = objectWriter.writeValueAsString(errorResponse)

    @Throws(JsonProcessingException::class)
    private fun asString(responseEntity: ResponseEntity<ErrorResponse>) = objectWriter.writeValueAsString(responseEntity.body!!)
}
