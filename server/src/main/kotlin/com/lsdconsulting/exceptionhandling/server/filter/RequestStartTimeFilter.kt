package com.lsdconsulting.exceptionhandling.server.filter

import com.lsdconsulting.exceptionhandling.server.config.attribute.REST_REQUEST_RECEIVED_AT_ATTRIBUTE
import com.lsdconsulting.exceptionhandling.server.time.TimeProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

class RequestStartTimeFilter(
    private val timeProvider: TimeProvider
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    public override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        request.setAttribute(REST_REQUEST_RECEIVED_AT_ATTRIBUTE, timeProvider.get().format(ISO_OFFSET_DATE_TIME))
        filterChain.doFilter(request, response)
    }
}
