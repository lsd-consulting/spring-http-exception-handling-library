package com.lsdconsulting.exceptionhandling.server.time

import java.time.ZonedDateTime

interface TimeProvider {
    fun get(): ZonedDateTime
}
