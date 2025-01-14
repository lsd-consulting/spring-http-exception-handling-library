package com.lsdconsulting.exceptionhandling.api

data class DataError(
    val code: String?,
    val name: String?,
    val value: String?,
    val message: String?,
)
