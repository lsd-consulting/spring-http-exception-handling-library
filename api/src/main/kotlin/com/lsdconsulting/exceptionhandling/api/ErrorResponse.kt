package com.lsdconsulting.exceptionhandling.api

import java.io.Serializable

data class ErrorResponse(
    val errorCode: String? = null,
    val messages: List<String> = listOf(),
    val dataErrors: List<DataError> = listOf(),
    val attributes: Map<String, Any?> = mapOf()
) : Serializable {

    constructor(errorCode: String? = null, message: String, dataErrors: List<DataError> = listOf(), attributes: Map<String, Any?> = mapOf()):
            this(errorCode, listOf(message), dataErrors, attributes)
}
