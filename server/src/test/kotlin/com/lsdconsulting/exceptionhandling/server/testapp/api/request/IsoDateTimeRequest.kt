package com.lsdconsulting.exceptionhandling.server.testapp.api.request

import org.springframework.format.annotation.DateTimeFormat
import java.time.ZonedDateTime
import javax.validation.constraints.NotNull

data class IsoDateTimeRequest(
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @field:NotNull(message = "validation.missingValue")
    val isoDateTime:  ZonedDateTime? = null
)
