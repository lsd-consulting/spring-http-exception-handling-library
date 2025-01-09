package com.lsdconsulting.exceptionhandling.server.testapp.api.request

import jakarta.validation.constraints.NotNull
import org.springframework.format.annotation.DateTimeFormat
import java.time.ZonedDateTime

data class IsoDateTimeRequest(
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @field:NotNull(message = "validation.missingValue")
    val isoDateTime: ZonedDateTime? = null
)
