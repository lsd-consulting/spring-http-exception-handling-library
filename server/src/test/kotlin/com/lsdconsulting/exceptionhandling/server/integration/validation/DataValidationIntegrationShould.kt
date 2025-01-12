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
internal class DataValidationIntegrationShould(
    @Autowired private val testRestTemplate: TestRestTemplate
) {

    private val objectWriter = objectMapper.writerWithDefaultPrettyPrinter()

    @Test
    internal fun `return 400 for http message not readable exception missing body`(approver: Approver) {
        val responseEntity = post(null)
        assertThat(responseEntity.statusCode, `is`(BAD_REQUEST))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    internal fun `return 400 for method argument not valid exception single property invalid data`(approver: Approver) {
        val responseEntity = post(TestRequest(message = "b", number = 5L))
        assertThat(responseEntity.statusCode, `is`(BAD_REQUEST))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    internal fun `return 400 for method argument not valid exception single property missing data`(approver: Approver) {
        val responseEntity = post(TestRequest(number = 5L))
        assertThat(responseEntity.statusCode, `is`(BAD_REQUEST))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    internal fun `return 400 for method argument not valid exception single property null`(approver: Approver) {
        val responseEntity = post(TestRequest(message = null, number = 5L))
        assertThat(responseEntity.statusCode, `is`(BAD_REQUEST))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    internal fun `return 400 for method argument not valid exception multiple property invalid data`(approver: Approver) {
        val responseEntity = post(TestRequest(message = "b", number = 3L))
        assertThat(responseEntity.statusCode, `is`(BAD_REQUEST))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    internal fun `return 400 for method argument not valid exception multiple property missing data`(approver: Approver) {
        val responseEntity = post(TestRequest())
        assertThat(responseEntity.statusCode, `is`(BAD_REQUEST))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    internal fun `return 400 for method argument not valid exception missing iso date time`(approver: Approver) {
        val responseEntity = postIsoDateTime(IsoDateTimeRequest())
        assertThat(responseEntity.statusCode, `is`(BAD_REQUEST))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    internal fun `return 400 for multiple wrong query params`(approver: Approver) {
        val responseEntity = getMultipleQueryParams()
        assertThat(responseEntity.statusCode, `is`(BAD_REQUEST))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    private fun post(testRequest: TestRequest?): ResponseEntity<ErrorResponse> {
        val headers = HttpHeaders()
        headers.contentType = APPLICATION_JSON
        val requestEntity = HttpEntity(testRequest, headers)
        return testRestTemplate.exchange("/objects", POST, requestEntity, ErrorResponse::class.java)
    }

    private fun getMultipleQueryParams(): ResponseEntity<ErrorResponse> {
        return testRestTemplate.getForEntity("/objects/multipleParams?someStringParam=1234&someNumericParam=4", ErrorResponse::class.java)
    }

    private fun postIsoDateTime(testRequest: IsoDateTimeRequest) =
        testRestTemplate.exchange("/objects/isodatetime", POST, HttpEntity(testRequest), ErrorResponse::class.java)

    @Throws(JsonProcessingException::class)
    private fun asString(errorResponse: ErrorResponse) = objectWriter.writeValueAsString(errorResponse)
}
