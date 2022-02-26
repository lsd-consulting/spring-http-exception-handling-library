package com.lsdconsulting.exceptionhandling.client.config

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import com.lsdconsulting.exceptionhandling.api.mapper.ObjectMapperBuilder.objectMapper
import java.io.IOException

object ErrorResponseFactory {

    @Throws(IOException::class)
    fun from(json: String): ErrorResponse {
        return objectMapper.readValue(json, ErrorResponse::class.java)
    }
}
