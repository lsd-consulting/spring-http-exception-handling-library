package com.lsdconsulting.exceptionhandling.server.testapp.api.response

import java.time.ZonedDateTime

data class TestResponse(
    var id: Long,
    var message: String? = null,
    var number: Long? = null,
    var created: ZonedDateTime
)
