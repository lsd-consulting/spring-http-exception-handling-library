package com.lsdconsulting.exceptionhandling.server.tracer

import brave.Tracer
import org.springframework.boot.actuate.autoconfigure.tracing.BraveAutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

@Import(BraveAutoConfiguration::class)
class BraveRequestTracerConfiguration {
    @Bean
    @ConditionalOnBean(Tracer::class)
    fun requestTracer(tracer: Tracer): RequestTracer {
        return RequestTracer {
            val currentSpan = tracer.currentSpan()
            val context = currentSpan?.context()
            return@RequestTracer context?.traceIdString()
        }
    }
}
