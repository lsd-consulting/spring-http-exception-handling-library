package com.lsdconsulting.exceptionhandling.api

import java.io.Serializable

data class DataError(
    val code: String? = null,
    val name: String? = null,
    val value: String? = null,
    val message: String? = null
) : Serializable
