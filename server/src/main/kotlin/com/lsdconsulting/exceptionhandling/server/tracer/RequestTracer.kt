package com.lsdconsulting.exceptionhandling.server.tracer

interface RequestTracer {
    fun getTraceId(): String?
}