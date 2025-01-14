package com.lsdconsulting.exceptionhandling.server.integration.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor

class TestRestTemplateConfiguration(
    @Value("\${common.platform.service.test.url}") private val rootUri: String
) {

    @Bean
    fun restTemplate(restTemplateBuilder: RestTemplateBuilder) = TestRestTemplate(
        restTemplateBuilder.rootUri(rootUri)
            .additionalInterceptors(ClientHttpRequestInterceptor { request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution ->
                request.headers[TRACE_ID_HEADER_NAME] = TRACE_ID
                execution.execute(request, body)
            })
    )

    companion object {
        private const val TRACE_ID = "40e1488ed0001adc-40e1488ed0001adc-1"
        private const val TRACE_ID_HEADER_NAME = "b3"
    }
}
