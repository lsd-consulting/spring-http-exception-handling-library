package com.lsdconsulting.exceptionhandling.server.tracer

import brave.Tracer
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.cloud.sleuth.autoconfig.brave.BraveAutoConfiguration
import org.springframework.cloud.sleuth.autoconfig.instrument.web.TraceWebAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

@Import(BraveAutoConfiguration::class, TraceWebAutoConfiguration::class)
class SleuthRequestTracerConfiguration {
    @Bean
    @ConditionalOnBean(Tracer::class)
    fun requestTracer(tracer: Tracer): RequestTracer {
        return object : RequestTracer {
            override fun getTraceId(): String? {
                val currentSpan = tracer.currentSpan()
                val context = currentSpan?.context()
                return context?.traceIdString()
            }
        }
    }
}