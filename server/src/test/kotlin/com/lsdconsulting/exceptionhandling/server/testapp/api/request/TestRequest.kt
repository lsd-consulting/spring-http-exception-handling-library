package com.lsdconsulting.exceptionhandling.server.testapp.api.request

import jakarta.validation.constraints.*

data class TestRequest(
    @field:Size(min = 2, max = 10, message = "validation.wrongSize")
    @field:NotBlank(message = "validation.missingValue")
    @field:Pattern(regexp = "aaa", message = "validation.wrongPattern")
    val message: String? = null,

    @field:NotNull(message = "validation.missingValue")
    @field:Min(value = 5, message = "validation.lessThanAcceptedMinimum")
    @field:Max(value = 10, message = "validation.moreThanAcceptedMaximum")
    val number: Long? = null
)
