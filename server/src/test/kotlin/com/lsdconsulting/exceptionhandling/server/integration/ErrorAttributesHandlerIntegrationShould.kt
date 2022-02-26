package com.lsdconsulting.exceptionhandling.server.integration

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
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.test.context.TestPropertySource
import java.io.IOException

@SpringBootTest(webEnvironment = DEFINED_PORT, classes = [TestApplication::class])
@ExtendWith(ResourcesApprovalsExtension::class)
@TestPropertySource("classpath:application-test.properties")
@EnableFeignClients(clients = [TestClient::class])
@Import(IntegrationTestConfiguration::class)
class ErrorAttributesHandlerIntegrationShould(
    @Autowired private val testRestTemplate: TestRestTemplate
) {

    private val objectWriter = objectMapper.writerWithDefaultPrettyPrinter()

    @Test
    @Throws(IOException::class)
    fun return404ReplyWithErrorBody(approver: Approver) {
        val responseEntity = testRestTemplate.getForEntity("/non-existent-resource", ErrorResponse::class.java)
        assertThat(responseEntity.statusCode, `is`(NOT_FOUND))
        approver.assertApproved(asString(responseEntity))
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun return404ReplyWithErrorBodyForBrowserRequest(approver: Approver) {
        val headers = HttpHeaders()
        headers["Accept"] = "text/html,application/xhtml+xml,application/xml"
        val entity = HttpEntity<String>(headers)
        val responseEntity = testRestTemplate.exchange("/non-existent-resource", GET, entity, ErrorResponse::class.java)
        assertThat(responseEntity.statusCode, `is`(NOT_FOUND))
        approver.assertApproved(asString(responseEntity))
    }

    @Throws(JsonProcessingException::class)
    private fun asString(responseEntity: ResponseEntity<ErrorResponse>) = objectWriter.writeValueAsString(responseEntity.body)
}
