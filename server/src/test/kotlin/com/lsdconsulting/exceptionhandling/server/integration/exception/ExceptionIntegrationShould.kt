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
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.test.context.TestPropertySource
import java.io.IOException

@Import(IntegrationTestConfiguration::class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, classes = [TestApplication::class])
@ExtendWith(ResourcesApprovalsExtension::class)
@TestPropertySource("classpath:application-test.properties")
@EnableFeignClients(clients = [TestClient::class])
class ExceptionIntegrationShould(
    @Autowired private val testRestTemplate: TestRestTemplate
) {

    private val objectWriter = objectMapper.writerWithDefaultPrettyPrinter()

    @Test
    @Throws(IOException::class)
    fun return500TestException(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/objects/generateTestException", ErrorResponse::class.java)
        assertThat(responseEntity.statusCode, `is`(INTERNAL_SERVER_ERROR))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    fun return500TestExceptionWithParam(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity(
            "/objects/generateTestExceptionWithCustomParam?someId=99999999",
            ErrorResponse::class.java
        )

        assertThat(responseEntity.statusCode, `is`(INTERNAL_SERVER_ERROR))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    fun return500Exception(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/objects/generateException", ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(INTERNAL_SERVER_ERROR))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    fun return500ExceptionDisregardingResponseStatusAnnotatedResource(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity(
            "/objects/generateExceptionWithAnnotatedStatusResource",
            ErrorResponse::class.java
        )

        assertThat(responseEntity.statusCode, `is`(INTERNAL_SERVER_ERROR))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    fun return404TestObjectNotFoundException(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/objects/objectNotFoundException", ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(NOT_FOUND))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    fun return405ForHttpRequestMethodNotSupportedException(approver: Approver) {
        val responseEntity = testRestTemplate.postForEntity("/objects/1", TestRequest(), ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(METHOD_NOT_ALLOWED))
        approver.assertApproved(asString(responseEntity))
    }

    @Test
    @Throws(IOException::class)
    fun return409AnnotatedStatusOnException(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/objects/generateAnnotatedException", ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(CONFLICT))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    fun return409AnnotatedStatusOnExceptionWithMessage(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/objects/generateAnnotatedExceptionWithMessage", ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(CONFLICT))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    fun return507ResponseStatusExceptionWithMessage(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/objects/generateResponseStatusException", ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(INSUFFICIENT_STORAGE))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    fun return507ResponseStatusExceptionWithoutMessage(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/objects/generateResponseStatusExceptionNoMessage", ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(INSUFFICIENT_STORAGE))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    @Throws(IOException::class)
    fun return507AnnotatedResponseStatusException(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/objects/generateAnnotatedResponseStatusException", ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(INSUFFICIENT_STORAGE))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Throws(JsonProcessingException::class)
    private fun asString(errorResponse: ErrorResponse): String {
        return objectWriter.writeValueAsString(errorResponse)
    }

    @Throws(JsonProcessingException::class)
    private fun asString(responseEntity: ResponseEntity<ErrorResponse>): String {
        return objectWriter.writeValueAsString(responseEntity.body!!)
    }
}
