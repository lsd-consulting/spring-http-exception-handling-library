package com.lsdconsulting.exceptionhandling.server.integration.validation

import com.fasterxml.jackson.core.JsonProcessingException
import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import com.lsdconsulting.exceptionhandling.api.mapper.ObjectMapperBuilder.objectMapper
import com.lsdconsulting.exceptionhandling.server.exension.ResourcesApprovalsExtension
import com.lsdconsulting.exceptionhandling.server.integration.config.IntegrationTestConfiguration
import com.lsdconsulting.exceptionhandling.server.testapp.TestApplication
import com.lsdconsulting.exceptionhandling.server.testapp.api.request.IsoDateTimeRequest
import com.lsdconsulting.exceptionhandling.server.testapp.api.request.TestRequest
import com.lsdconsulting.exceptionhandling.server.testapp.client.TestClient
import com.oneeyedmen.okeydoke.Approver
import org.hamcrest.CoreMatchers.`is`
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
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.ResponseEntity
import org.springframework.test.context.TestPropertySource

@SpringBootTest(webEnvironment = DEFINED_PORT, classes = [TestApplication::class])
@ExtendWith(ResourcesApprovalsExtension::class)
@TestPropertySource("classpath:application-test.properties")
@EnableFeignClients(clients = [TestClient::class])
@Import(IntegrationTestConfiguration::class)
@AutoConfigureObservability
class DataValidationIntegrationShould(
    @Autowired private val testRestTemplate: TestRestTemplate
) {

    private val objectWriter = objectMapper.writerWithDefaultPrettyPrinter()

    @Test
    fun return400ForHttpMessageNotReadableException_MissingBody(approver: Approver) {
        val responseEntity = post(null)
        assertThat(responseEntity.statusCode, `is`(BAD_REQUEST))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    fun return400ForMethodArgumentNotValidException_SinglePropertyInvalidData(approver: Approver) {
        val responseEntity = post(TestRequest(message = "b", number = 5L))
        assertThat(responseEntity.statusCode, `is`(BAD_REQUEST))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    fun return400ForMethodArgumentNotValidException_SinglePropertyMissingData(approver: Approver) {
        val responseEntity = post(TestRequest(number = 5L))
        assertThat(responseEntity.statusCode, `is`(BAD_REQUEST))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    fun return400ForMethodArgumentNotValidException_MultiplePropertyInvalidData(approver: Approver) {
        val responseEntity = post(TestRequest(message = "b", number = 3L))
        assertThat(responseEntity.statusCode, `is`(BAD_REQUEST))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    fun return400ForMethodArgumentNotValidException_MultiplePropertyMissingData(approver: Approver) {
        val responseEntity = post(TestRequest())
        assertThat(responseEntity.statusCode, `is`(BAD_REQUEST))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    fun return400ForMethodArgumentNotValidException_MissingIsoDateTime(approver: Approver) {
        val responseEntity = postIsoDateTime(IsoDateTimeRequest())
        assertThat(responseEntity.statusCode, `is`(BAD_REQUEST))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    private fun post(testRequest: TestRequest?): ResponseEntity<ErrorResponse> {
        val headers = HttpHeaders()
        headers.contentType = APPLICATION_JSON
        val requestEntity = HttpEntity(testRequest, headers)
        return testRestTemplate.exchange("/objects", POST, requestEntity, ErrorResponse::class.java)
    }

    private fun postIsoDateTime(testRequest: IsoDateTimeRequest): ResponseEntity<ErrorResponse> {
        return testRestTemplate.exchange("/objects/isodatetime", POST, HttpEntity(testRequest), ErrorResponse::class.java)
    }

    @Throws(JsonProcessingException::class)
    private fun asString(errorResponse: ErrorResponse) = objectWriter.writeValueAsString(errorResponse)
}
