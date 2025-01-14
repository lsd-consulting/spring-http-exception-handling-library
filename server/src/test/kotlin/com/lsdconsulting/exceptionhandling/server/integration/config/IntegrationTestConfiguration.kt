package com.lsdconsulting.exceptionhandling.server.integration.config

import com.lsdconsulting.exceptionhandling.server.config.ErrorViewConfiguration
import com.lsdconsulting.exceptionhandling.server.filter.RequestFilterConfiguration
import com.lsdconsulting.exceptionhandling.server.tracer.BraveRequestTracerConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    ErrorViewConfiguration::class,
    BraveRequestTracerConfiguration::class,
    RequestFilterConfiguration::class,
    ConstantTimeProviderConfiguration::class,
    TestRestTemplateConfiguration::class
)
class IntegrationTestConfiguration
