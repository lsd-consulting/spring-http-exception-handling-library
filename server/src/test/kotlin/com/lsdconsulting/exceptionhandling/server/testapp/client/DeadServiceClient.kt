package com.lsdconsulting.exceptionhandling.server.testapp.client

import com.lsdconsulting.exceptionhandling.client.config.ClientConfiguration
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

/**
 * Useful to test what happens when we hit endpoints that are not up etc.
 */
@FeignClient(name = "deadServiceClient", url = "http://localhost:60123", configuration = [ClientConfiguration::class])
interface DeadServiceClient {
    @get:GetMapping("/anything")
    val anything: Unit
}
