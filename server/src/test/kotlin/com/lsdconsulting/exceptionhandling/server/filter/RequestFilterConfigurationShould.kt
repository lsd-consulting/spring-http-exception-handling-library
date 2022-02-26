package com.lsdconsulting.exceptionhandling.server.filter

import com.lsdconsulting.exceptionhandling.server.time.DefaultTimeProviderConfiguration
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.collection.IsMapContaining.hasKey
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext

internal class RequestFilterConfigurationShould {

    @Test
    fun registerRequestStartTimeFilter() {
        AnnotationConfigServletWebServerApplicationContext().use { context ->
            TestPropertyValues.of("server.port=0").applyTo(context)
            context.register(
                DefaultTimeProviderConfiguration::class.java,
                ServletWebServerFactoryAutoConfiguration::class.java,
                RequestFilterConfiguration::class.java
            )
            context.refresh()
            val servletContext = context.servletContext!!
            assertThat(servletContext, notNullValue())
            val filterRegistrations = servletContext.filterRegistrations
            assertThat(filterRegistrations, hasKey("requestStartTimeFilter"))
            assertThat(filterRegistrations["requestStartTimeFilter"]!!.className, `is`(RequestStartTimeFilter::class.java.name))
        }
    }
}
