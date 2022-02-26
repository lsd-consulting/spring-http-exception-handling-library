package com.lsdconsulting.exceptionhandling.server.integration.config

import com.lsdconsulting.exceptionhandling.server.time.TimeProvider
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@TestConfiguration
class ConstantTimeProviderConfiguration {
    @Bean
    fun timeProvider(): TimeProvider {
        return object : TimeProvider {
            override fun get(): ZonedDateTime {
                return ZonedDateTime.parse(TIME_STAMP, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            }
        }
    }

    companion object {
        private const val TIME_STAMP = "2020-11-27T11:17:40.095818Z"
    }
}
