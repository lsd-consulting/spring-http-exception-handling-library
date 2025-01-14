package com.lsdconsulting.exceptionhandling.server.time

import java.time.ZonedDateTime

fun interface TimeProvider {
    fun get(): ZonedDateTime
}
