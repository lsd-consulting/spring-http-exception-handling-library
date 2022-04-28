package com.lsdconsulting.exceptionhandling.server.time

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import java.time.ZoneId
import java.time.ZonedDateTime

class DefaultTimeProviderConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun timeProvider(): TimeProvider {
        return object : TimeProvider {
            override fun get(): ZonedDateTime {
                return ZonedDateTime.now(ZoneId.of("UTC"))
            }
        }
    }
}
