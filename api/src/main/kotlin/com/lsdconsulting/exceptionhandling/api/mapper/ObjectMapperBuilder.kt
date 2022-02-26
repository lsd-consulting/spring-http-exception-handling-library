package com.lsdconsulting.exceptionhandling.api.mapper

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object ObjectMapperBuilder {
    val objectMapper = jacksonObjectMapper()

    init {
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.configure(WRITE_DATES_AS_TIMESTAMPS, false)
        objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
}
