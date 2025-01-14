package com.lsdconsulting.exceptionhandling.server.tracer

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

class DefaultRequestTracerConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun requestTracer(): RequestTracer {
        return RequestTracer { null }
    }
}
