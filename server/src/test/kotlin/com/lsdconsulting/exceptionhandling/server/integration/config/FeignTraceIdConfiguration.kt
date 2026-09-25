package com.lsdconsulting.exceptionhandling.server.integration.config

import feign.RequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignTraceIdConfiguration {
    @Bean
    fun fixedTraceIdRequestInterceptor(): RequestInterceptor =
        RequestInterceptor { template ->
            template.header(TRACE_ID_HEADER_NAME, TRACE_ID)
        }

    companion object {
        // Same fixed b3 value used by TestRestTemplateConfiguration for stable approval baselines
        const val TRACE_ID = "40e1488ed0001adc-40e1488ed0001adc-1"
        const val TRACE_ID_HEADER_NAME = "b3"
    }
}
