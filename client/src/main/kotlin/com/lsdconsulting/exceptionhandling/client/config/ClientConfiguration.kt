package com.lsdconsulting.exceptionhandling.client.config

import com.lsdconsulting.exceptionhandling.api.mapper.ObjectMapperBuilder.objectMapper
import feign.Retryer
import feign.codec.ErrorDecoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

@Configuration
class ClientConfiguration {

    @Bean
    fun errorDecoder(): ErrorDecoder = ClientErrorDecoder()

    /**
     * Ensure Feign uses the library's configured Jackson ObjectMapper
     * (Boot 4 / OpenFeign 5 no longer take ObjectFactory&lt;HttpMessageConverters&gt;).
     */
    @Bean
    fun feignJacksonMessageConverterCustomizer(): HttpMessageConverterCustomizer =
        HttpMessageConverterCustomizer { converters ->
            converters.removeAll { it is MappingJackson2HttpMessageConverter }
            converters.add(0, MappingJackson2HttpMessageConverter(objectMapper))
        }

    @Bean
    fun feignRetryer(
        @Value("\${feign.retry.period:500}") retryInitialPeriod: Long,
        @Value("\${feign.retry.maxPeriod:1000}") retryMaxPeriod: Long,
        @Value("\${feign.retry.maxAttempts:5}") retryMaxAttempts: Int,
    ) = Retryer.Default(retryInitialPeriod, retryMaxPeriod, retryMaxAttempts)
}
