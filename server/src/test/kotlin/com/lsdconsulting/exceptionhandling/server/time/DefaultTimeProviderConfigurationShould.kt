package com.lsdconsulting.exceptionhandling.server.time

import org.exparity.hamcrest.date.ZonedDateTimeMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

internal class DefaultTimeProviderConfigurationShould {
    private val custom = ZonedDateTime.of(LocalDateTime.MIN, ZoneId.of("UTC"))
    private lateinit var context: AnnotationConfigApplicationContext

    @AfterEach
    internal fun `close context`() {
        context.close()
    }

    @Test
    internal fun `not register if time provider present`() {
        context = load(true)
        assertThat(context.getBean(TimeProvider::class.java).get(), `is`(custom))
    }

    @Test
    internal fun `register if time provider present`() {
        context = load(false)
        val now = ZonedDateTime.now()
        assertThat(context.getBean(TimeProvider::class.java).get(), ZonedDateTimeMatchers.after(now))
    }

    private fun load(registerTracerBean: Boolean): AnnotationConfigApplicationContext {
        val ctx = AnnotationConfigApplicationContext()
        if (registerTracerBean) {
            ctx.defaultListableBeanFactory.registerSingleton("timeProvider", object : TimeProvider {
                override fun get(): ZonedDateTime {
                    return custom
                }
            } as TimeProvider)
        }
        ctx.register(DefaultTimeProviderConfiguration::class.java)
        ctx.refresh()
        return ctx
    }
}
