package com.lsdconsulting.exceptionhandling.server.filter

import com.lsdconsulting.exceptionhandling.server.time.TimeProvider
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.time.format.DateTimeFormatter
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RequestStartTimeFilter(
    private val timeProvider: TimeProvider
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    public override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        request.setAttribute(START_TIME_ATTRIBUTE, timeProvider.get().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        filterChain.doFilter(request, response)
    }

    companion object {
        const val START_TIME_ATTRIBUTE = "rest.request.receivedAt"
    }
}
