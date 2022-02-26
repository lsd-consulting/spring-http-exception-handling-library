package com.lsdconsulting.exceptionhandling.server.testapp.client

import com.lsdconsulting.exceptionhandling.client.config.ClientConfiguration
import com.lsdconsulting.exceptionhandling.client.exception.ErrorResponseException
import com.lsdconsulting.exceptionhandling.server.testapp.api.request.TestRequest
import com.lsdconsulting.exceptionhandling.server.testapp.api.response.TestResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "testClient",
    url = "\${common.platform.service.test.url}",
    configuration = [ClientConfiguration::class]
)
interface TestClient {
    @PostMapping("/objects")
    @Throws(ErrorResponseException::class)
    fun postObject(testRequest: TestRequest?): TestResponse?

    @GetMapping("/objects/{objectId}")
    @Throws(ErrorResponseException::class)
    fun getObject(@PathVariable(name = "objectId") objectId: Long): TestResponse?

    @GetMapping("/objects")
    @Throws(ErrorResponseException::class)
    fun getObjectByMessage(@RequestParam(name = "message") message: String?): List<TestResponse?>?

    @get:Throws(ErrorResponseException::class)
    @get:GetMapping("/objects/generateTestException")
    val withTestException: TestResponse?

    @GetMapping("/objects/generateTestExceptionWithCustomParam")
    @Throws(ErrorResponseException::class)
    fun getWithExceptionAndCustomParam(@RequestParam(name = "someId") someId: Long?): TestResponse?

    @get:Throws(ErrorResponseException::class)
    @get:GetMapping("/objects/generateException")
    val withException: TestResponse?

    @get:Throws(ErrorResponseException::class)
    @get:GetMapping("/objects/generateCheckedException")
    val withCheckedException: TestResponse?

    @get:Throws(ErrorResponseException::class)
    @get:GetMapping("/objects/generateExceptionWithAnnotatedStatusResource")
    val withExceptionAndAnnotatedStatusResource: TestResponse?

    @get:Throws(ErrorResponseException::class)
    @get:GetMapping("/objects/generateAnnotatedException")
    val withAnnotatedException: TestResponse?

    @get:Throws(ErrorResponseException::class)
    @get:GetMapping("/objects/objectNotFoundException")
    val withNotFoundException: TestResponse?

    @get:Throws(ErrorResponseException::class)
    @get:GetMapping("/objects/conflict")
    val withConflictException: TestResponse?

    @get:Throws(ErrorResponseException::class)
    @get:GetMapping("/objects/preconditionFailed")
    val withPreconditionFailedException: TestResponse?

    @GetMapping("/objects/malformedResponse")
    @Throws(ErrorResponseException::class)
    fun getMalformedResponse(@RequestParam("responseCode") responseCode: Int): TestResponse?

    @GetMapping("/objects/emptyResponse")
    @Throws(ErrorResponseException::class)
    fun getEmptyResponse(@RequestParam("responseCode") responseCode: Int): TestResponse?
}
