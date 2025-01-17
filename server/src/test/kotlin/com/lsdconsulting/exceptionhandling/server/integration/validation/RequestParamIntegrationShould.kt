package com.lsdconsulting.exceptionhandling.server.integration.validation

import com.fasterxml.jackson.core.JsonProcessingException
import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import com.lsdconsulting.exceptionhandling.api.mapper.ObjectMapperBuilder.objectMapper
import com.lsdconsulting.exceptionhandling.server.exension.ResourcesApprovalsExtension
import com.lsdconsulting.exceptionhandling.server.integration.config.IntegrationTestConfiguration
import com.lsdconsulting.exceptionhandling.server.testapp.TestApplication
import com.lsdconsulting.exceptionhandling.server.testapp.client.TestClient
import com.oneeyedmen.okeydoke.Approver
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON
import org.springframework.test.context.TestPropertySource

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, classes = [TestApplication::class])
@ExtendWith(ResourcesApprovalsExtension::class)
@TestPropertySource("classpath:application-test.properties")
@EnableFeignClients(clients = [TestClient::class])
@Import(IntegrationTestConfiguration::class)
@AutoConfigureObservability
internal class RequestParamIntegrationShould(
    @Autowired private val testRestTemplate: TestRestTemplate
) {

    private val objectWriter = objectMapper.writerWithDefaultPrettyPrinter()

    @Test
    internal fun `return 400 for missing servlet request parameter exception`(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/objects", ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(BAD_REQUEST))
        assertThat(responseEntity.headers.contentType, `is`(APPLICATION_PROBLEM_JSON))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Test
    internal fun `return 400 for constraint violation exception`(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/objects?someParamName=abcdef", ErrorResponse::class.java)

        assertThat(responseEntity.statusCode, `is`(BAD_REQUEST))
        assertThat(responseEntity.headers.contentType, `is`(APPLICATION_PROBLEM_JSON))
        approver.assertApproved(asString(responseEntity.body!!))
    }

    @Throws(JsonProcessingException::class)
    private fun asString(errorResponse: ErrorResponse) = objectWriter.writeValueAsString(errorResponse)
}
