package com.lsdconsulting.exceptionhandling.server.config

import com.lsdconsulting.exceptionhandling.server.filter.RequestFilterConfiguration
import com.lsdconsulting.exceptionhandling.server.time.DefaultTimeProviderConfiguration
import com.lsdconsulting.exceptionhandling.server.tracer.DefaultRequestTracerConfiguration
import com.lsdconsulting.exceptionhandling.server.tracer.SleuthRequestTracerConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan(basePackageClasses = [ExceptionHandlerConfiguration::class])
@Import(
    ErrorViewConfiguration::class,
    SleuthRequestTracerConfiguration::class,
    RequestFilterConfiguration::class,
    DefaultRequestTracerConfiguration::class,
    DefaultTimeProviderConfiguration::class
)
class ExceptionHandlerConfiguration 