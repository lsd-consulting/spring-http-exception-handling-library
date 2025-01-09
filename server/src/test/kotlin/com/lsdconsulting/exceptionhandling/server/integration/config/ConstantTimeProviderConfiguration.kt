package com.lsdconsulting.exceptionhandling.server.integration.config

import com.lsdconsulting.exceptionhandling.server.time.TimeProvider
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

@TestConfiguration
class ConstantTimeProviderConfiguration {
    @Bean
    fun timeProvider() = TimeProvider { ZonedDateTime.parse(TIMESTAMP, ISO_OFFSET_DATE_TIME) }

    companion object {
        private const val TIMESTAMP = "2020-11-27T11:17:40.095818Z"
    }
}
