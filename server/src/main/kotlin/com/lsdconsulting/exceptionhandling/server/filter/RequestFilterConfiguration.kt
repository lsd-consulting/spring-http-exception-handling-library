package com.lsdconsulting.exceptionhandling.server.filter

import com.lsdconsulting.exceptionhandling.server.time.TimeProvider
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE

class RequestFilterConfiguration(
    private val timeProvider: TimeProvider
) {

    @Bean
    fun registerRequestStartTimeFilter(): FilterRegistrationBean<RequestStartTimeFilter> {
        val reg = FilterRegistrationBean(RequestStartTimeFilter(timeProvider))
        reg.order = HIGHEST_PRECEDENCE
        return reg
    }
}
