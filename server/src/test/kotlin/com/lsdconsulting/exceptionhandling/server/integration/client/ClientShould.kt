package com.lsdconsulting.exceptionhandling.server.integration.client

import com.lsdconsulting.exceptionhandling.client.exception.ErrorResponseException
import com.lsdconsulting.exceptionhandling.server.exension.ResourcesApprovalsExtension
import com.lsdconsulting.exceptionhandling.server.integration.client.ClientShould.RequestCountingConfig
import com.lsdconsulting.exceptionhandling.server.integration.config.IntegrationTestConfiguration
import com.lsdconsulting.exceptionhandling.server.testapp.TestApplication
import com.lsdconsulting.exceptionhandling.server.testapp.client.DeadServiceClient
import com.lsdconsulting.exceptionhandling.server.testapp.client.TestClient
import feign.RequestInterceptor
import feign.RetryableException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import java.util.concurrent.atomic.AtomicInteger

/**
 * This test spins up a separate context (due to the extra RequestCountingConfig required to verify the number of request attempts).
 */
@SpringBootTest(webEnvironment = DEFINED_PORT, classes = [TestApplication::class])
@ExtendWith(ResourcesApprovalsExtension::class)
@TestPropertySource("classpath:application-retry.properties")
@EnableFeignClients(clients = [DeadServiceClient::class, TestClient::class])
@Import(IntegrationTestConfiguration::class, RequestCountingConfig::class)
@AutoConfigureObservability
internal class ClientShould(
    @Value("\${feign.retry.maxAttempts}") private val maxAttempts: Int = 0,
    @Autowired private  val deadServiceClient: DeadServiceClient,
    @Autowired private  val testClient: TestClient,
    @Autowired private  val attemptCounter: AtomicInteger,
) {

    @BeforeEach
    fun setUp() = attemptCounter.set(0)

    @Test
    internal fun `retry when the endpoint is unreachable`() {
        assertThrows(RetryableException::class.java) { deadServiceClient.anything }
        assertThat(attemptCounter.get(), equalTo(maxAttempts))
    }

    @Test
    internal fun `not retry on4xx errors`() {
        assertThrows(ErrorResponseException::class.java) { testClient.withNotFoundException }
        assertThat(attemptCounter.get(), equalTo(1))
    }

    @Test
    internal fun `not retry on5xx errors`() {
        assertThrows(ErrorResponseException::class.java) { testClient.withException }
        assertThat(attemptCounter.get(), equalTo(1))
    }

    internal class RequestCountingConfig {
        @Bean
        fun attemptCounter() = AtomicInteger(0)

        @Bean
        fun requestInterceptor(attemptCounter: AtomicInteger) = RequestInterceptor { attemptCounter.incrementAndGet() }
    }
}
