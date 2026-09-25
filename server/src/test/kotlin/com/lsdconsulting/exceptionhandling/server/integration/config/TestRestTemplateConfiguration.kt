package com.lsdconsulting.exceptionhandling.server.integration.config

import com.lsdconsulting.exceptionhandling.api.mapper.ObjectMapperBuilder.objectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.restclient.RestTemplateBuilder
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpRequest
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

class TestRestTemplateConfiguration(
    @Value("\${common.platform.service.test.url}") private val rootUri: String
) {

    @Bean
    fun restTemplate(restTemplateBuilder: RestTemplateBuilder): TestRestTemplate {
        val jacksonConverter = MappingJackson2HttpMessageConverter(objectMapper).apply {
            supportedMediaTypes = listOf(
                APPLICATION_JSON,
                MediaType("application", "problem+json")
            )
        }
        val testRestTemplate = TestRestTemplate(
            restTemplateBuilder.rootUri(rootUri)
                .additionalInterceptors(ClientHttpRequestInterceptor { request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution ->
                    request.headers[TRACE_ID_HEADER_NAME] = TRACE_ID
                    execution.execute(request, body)
                })
        )
        // Swap Boot's Jackson converter for the library Kotlin-aware ObjectMapper, keeping converter order
        // so StringHttpMessageConverter still handles String responses (warmup GETs in some tests).
        val converters = testRestTemplate.restTemplate.messageConverters
        val jacksonIndex = converters.indexOfFirst { it is MappingJackson2HttpMessageConverter }
        if (jacksonIndex >= 0) {
            converters[jacksonIndex] = jacksonConverter
        } else {
            converters.add(jacksonConverter)
        }
        return testRestTemplate
    }

    companion object {
        private const val TRACE_ID = "40e1488ed0001adc-40e1488ed0001adc-1"
        private const val TRACE_ID_HEADER_NAME = "b3"
    }
}
