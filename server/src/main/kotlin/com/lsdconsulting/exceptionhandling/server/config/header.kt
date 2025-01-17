package com.lsdconsulting.exceptionhandling.server.config

import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

fun headerWithContentType(): MultiValueMap<String, String> {
    val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
    headers.add(CONTENT_TYPE, APPLICATION_PROBLEM_JSON_VALUE)
    return headers
}