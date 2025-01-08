package com.lsdconsulting.exceptionhandling.server.time

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import java.time.ZoneId
import java.time.ZonedDateTime.now

class DefaultTimeProviderConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun timeProvider(): TimeProvider {
        return TimeProvider { now(ZoneId.of("UTC")) }
    }
}
