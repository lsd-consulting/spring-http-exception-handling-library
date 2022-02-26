package com.lsdconsulting.exceptionhandling.server.integration.config

import com.lsdconsulting.exceptionhandling.server.config.ErrorViewConfiguration
import com.lsdconsulting.exceptionhandling.server.filter.RequestFilterConfiguration
import com.lsdconsulting.exceptionhandling.server.tracer.SleuthRequestTracerConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import javax.annotation.PostConstruct

@TestConfiguration
@Import(
    ErrorViewConfiguration::class,
    SleuthRequestTracerConfiguration::class,
    RequestFilterConfiguration::class,
    ConstantTimeProviderConfiguration::class
)
class IntegrationTestConfiguration(
    @Autowired private val testRestTemplate: TestRestTemplate
) {

    @PostConstruct
    fun setB3Header() {
        testRestTemplate.restTemplate.interceptors.add(
            ClientHttpRequestInterceptor { request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution ->
                request.headers.add(TRACE_ID_HEADER_NAME, TRACE_ID)
                execution.execute(request, body)
            }
        )
    }

    companion object {
        private const val TRACE_ID = "40e1488ed0001adc-40e1488ed0001adc-1"
        private const val TRACE_ID_HEADER_NAME = "b3"
    }
}
