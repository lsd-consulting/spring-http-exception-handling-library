package com.lsdconsulting.exceptionhandling.server.tracer

fun interface RequestTracer {
    fun getTraceId(): String?
}
