package com.lsdconsulting.exceptionhandling.server.tracer

import brave.Span
import brave.Tracer
import brave.propagation.TraceContext
import org.apache.commons.lang3.RandomUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.cloud.sleuth.autoconfig.instrument.web.TraceWebAutoConfiguration
import org.springframework.context.annotation.AnnotationConfigApplicationContext

internal class SleuthRequestTracerConfigurationShould {

    private val traceId = RandomUtils.nextLong(MIN_VALID_TRACE_ID_VALUE, Long.MAX_VALUE)
    private val traceIdString = java.lang.Long.toHexString(traceId)
    private val mockTracer = Mockito.mock(Tracer::class.java)
    private lateinit var context: AnnotationConfigApplicationContext

    @AfterEach
    fun closeContext() {
        context.close()
    }

    @Test
    fun notRegisterRequestTracerWhenNoTracerAvailable() {
        context = load(false)
        assertThat(context.getBean(RequestTracer::class.java).getTraceId(), Matchers.nullValue())
        Mockito.verifyNoInteractions(mockTracer)
    }

    @Test
    fun handleMissingSpan() {
        context = load(true)
        assertThat(context.getBean(RequestTracer::class.java).getTraceId(), Matchers.nullValue())
    }

    @Test
    fun registerRequestTracerWhenSleuthEnabled() {
        prepareTracer()
        context = load(true, "spring.sleuth.enabled=true")
        assertThat(context.getBean(RequestTracer::class.java).getTraceId(), `is`(traceIdString))
    }

    @Test
    fun notRegisterRequestTracerWhenSleuthEnabled() {
        prepareTracer()
        context = load(true, "spring.sleuth.enabled=true")
        assertThat(context.getBean(RequestTracer::class.java).getTraceId(), Matchers.notNullValue())
    }

    @Test
    fun registerRequestTracer() {
        prepareTracer()
        context = load(true)
        assertThat(context.getBean(RequestTracer::class.java).getTraceId(), `is`(traceIdString))
    }

    private fun load(registerTracerBean: Boolean, vararg properties: String): AnnotationConfigApplicationContext {
        val ctx = AnnotationConfigApplicationContext()
        TestPropertyValues.of(*properties).applyTo(ctx)
        if (registerTracerBean) {
            ctx.defaultListableBeanFactory.registerSingleton("mockTracer", mockTracer)
            ctx.register(TraceWebAutoConfiguration::class.java)
        }
        ctx.register(SleuthRequestTracerConfiguration::class.java)
        ctx.register(DefaultRequestTracerConfiguration::class.java)
        ctx.refresh()
        return ctx
    }

    private fun prepareTracer() {
        val mockSpan = Mockito.mock(Span::class.java)
        val mockTraceContext = TraceContext.newBuilder().spanId(1).traceId(traceId).build()
        Mockito.`when`(mockSpan.context()).thenReturn(mockTraceContext)
        Mockito.`when`(mockTracer.currentSpan()).thenReturn(mockSpan)
    }

    companion object {
        private const val MIN_VALID_TRACE_ID_VALUE = 1152921504606846976L
    }
}
