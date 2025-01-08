package com.lsdconsulting.exceptionhandling.server.tracer

import brave.Span
import brave.Tracer
import brave.propagation.TraceContext
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.actuate.autoconfigure.tracing.BraveAutoConfiguration
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.security.SecureRandom

@OptIn(ExperimentalStdlibApi::class)
internal class BraveRequestTracerConfigurationShould {
    private val traceId = SecureRandom().nextLong(MIN_VALID_TRACE_ID_VALUE, Long.MAX_VALUE)
    private val traceIdString: String = traceId.toHexString()
    private val mockTracer = mockk<Tracer>()

    private lateinit var context: AnnotationConfigApplicationContext

    @AfterEach
    fun closeContext() {
        context.close()
    }

    @Test
    internal fun `not register request tracer when no tracer available`() {
        context = load(false)

        assertThat(getTraceIdFromContext(), `is`(nullValue()))
        verify { mockTracer wasNot Called }
    }

    @Test
    internal fun `handle missing span`() {
        every { mockTracer.currentSpan() } returns null
        context = load(true)

        assertThat(getTraceIdFromContext(), `is`(nullValue()))
    }

    @Test
    internal fun `register request tracer when sleuth enabled`() {
        prepareTracer()
        context = load(true, "spring.sleuth.enabled=true")

        assertThat(getTraceIdFromContext(), `is`(traceIdString))
    }

    @Test
    internal fun `not register request tracer when sleuth enabled`() {
        prepareTracer()
        context = load(true, "spring.sleuth.enabled=true")

        assertThat(getTraceIdFromContext(), `is`(notNullValue()))
    }

    @Test
    internal fun `register request tracer`() {
        prepareTracer()
        context = load(true)

        assertThat(getTraceIdFromContext(), `is`(traceIdString))
    }

    private fun getTraceIdFromContext() = context.getBean(RequestTracer::class.java).getTraceId()

    private fun load(registerTracerBean: Boolean, vararg properties: String): AnnotationConfigApplicationContext {
        val ctx = AnnotationConfigApplicationContext()
        TestPropertyValues.of(*properties).applyTo(ctx)
        if (registerTracerBean) {
            ctx.defaultListableBeanFactory.registerSingleton("mockTracer", mockTracer)
            ctx.register(BraveAutoConfiguration::class.java)
        }
        ctx.register(BraveRequestTracerConfiguration::class.java)
        ctx.register(DefaultRequestTracerConfiguration::class.java)
        ctx.refresh()
        return ctx
    }

    private fun prepareTracer() {
        val mockSpan = mockk<Span>()
        val mockTraceContext = TraceContext.newBuilder().spanId(1).traceId(traceId).build()
        every { mockSpan.context() } returns mockTraceContext
        every { mockTracer.currentSpan() } returns mockSpan
    }

    companion object {
        private const val MIN_VALID_TRACE_ID_VALUE = 1152921504606846976L
    }
}
