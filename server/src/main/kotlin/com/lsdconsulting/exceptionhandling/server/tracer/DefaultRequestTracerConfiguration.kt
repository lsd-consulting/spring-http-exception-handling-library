package com.lsdconsulting.exceptionhandling.server.tracer

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

class DefaultRequestTracerConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun requestTracer(): RequestTracer {
        return object : RequestTracer {
            override fun getTraceId(): String? {
                return null
            }
        }
    }
}

