package com.lsdconsulting.exceptionhandling.client.config

import com.lsdconsulting.exceptionhandling.api.mapper.ObjectMapperBuilder.objectMapper
import feign.Retryer
import feign.codec.Decoder
import feign.codec.ErrorDecoder
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder
import org.springframework.cloud.openfeign.support.SpringDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

@Configuration
class ClientConfiguration {

    @Bean
    fun errorDecoder(): ErrorDecoder = ClientErrorDecoder()

    @Bean
    fun feignDecoder(): Decoder {
        val jacksonConverter = MappingJackson2HttpMessageConverter(objectMapper)
        val objectFactory = ObjectFactory { HttpMessageConverters(jacksonConverter) }
        return ResponseEntityDecoder(SpringDecoder(objectFactory))
    }

    @Bean
    fun feignRetryer(
        @Value("\${feign.retry.period:500}") retryInitialPeriod: Long,
        @Value("\${feign.retry.maxPeriod:1000}") retryMaxPeriod: Long,
        @Value("\${feign.retry.maxAttempts:5}") retryMaxAttempts: Int,
    ) = Retryer.Default(retryInitialPeriod, retryMaxPeriod, retryMaxAttempts)
}
