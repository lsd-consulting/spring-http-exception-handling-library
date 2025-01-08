package com.lsdconsulting.exceptionhandling.api

data class ErrorResponse(
    val errorCode: String? = null,
    val messages: List<String> = listOf(),
    val dataErrors: List<DataError> = listOf(),
    val attributes: Map<String, Any?> = mapOf()
)
