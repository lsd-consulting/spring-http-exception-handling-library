package com.lsdconsulting.exceptionhandling.server.testapp.controller

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import com.lsdconsulting.exceptionhandling.server.exception.ErrorResponseException
import com.lsdconsulting.exceptionhandling.server.testapp.api.exception.*
import com.lsdconsulting.exceptionhandling.server.testapp.api.request.IsoDateTimeRequest
import com.lsdconsulting.exceptionhandling.server.testapp.api.request.TestRequest
import com.lsdconsulting.exceptionhandling.server.testapp.api.response.TestResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime

@Validated
@RestController
@RequestMapping(path = ["/objects"])
class TestController {

    @PostMapping
    @ResponseStatus(CREATED)
    fun postObject(@Valid @RequestBody testRequest: TestRequest) = TestResponse(
        message = testRequest.message,
        number = testRequest.number,
        id = 1L,
        created = ZonedDateTime.now()
    )

    @PostMapping
    @ResponseStatus(CREATED)
    @RequestMapping(path = ["/isodatetime"])
    fun postIsoDatetime(@Valid @RequestBody isoDateTimeRequest: IsoDateTimeRequest) = TestResponse(
        message = isoDateTimeRequest.toString(),
        id = 1L,
        created = ZonedDateTime.now()
    )

    @GetMapping("/{objectId}")
    fun getObject(@PathVariable @Min(1000) @Positive objectId: Long) = TestResponse(
        message = "message",
        number = 5L,
        id = objectId,
        created = ZonedDateTime.now()
    )

    @GetMapping("/pathVariableValidation/{objectId}")
    fun getObjectByPathVariable(@PathVariable @Size(max = 3) objectId: String) = TestResponse(
        message = "message",
        number = 5L,
        id = objectId.toLong(),
        created = ZonedDateTime.now())

    @GetMapping
    fun getObjectBy(@RequestParam @Max(3) someParamName: String) = listOf(
        TestResponse(
            message = someParamName,
            number = 5L,
            id = 1L,
            created = ZonedDateTime.now())
    )

    @GetMapping("/multipleParams")
    fun getObjectByMultipleParams(@RequestParam @Size(max = 3) @NotBlank someStringParam: String,
                                  @RequestParam @Max(3) someNumericParam: Int) = listOf(
        TestResponse(
            message = "someMessage",
            number = 5L,
            id = 1L,
            created = ZonedDateTime.now())
    )

    @GetMapping("/generateTestException")
    fun getWithTestException(): Unit = throw TestException()

    @GetMapping("/generateTestExceptionWithCustomParam")
    fun getWithExceptionAndCustomParam(@RequestParam someId: Long): Unit =
        throw TestParameterException("someId", someId)

    @GetMapping("/generateException")
    fun getWithException(): Unit = throw RuntimeException("Some message")

    @Throws(Exception::class)
    @GetMapping("/generateCheckedException")
    fun getWithCheckedException(): Unit = throw Exception("Some checked exception message")

    @ResponseStatus(code = ACCEPTED, reason = "Just testing")
    @GetMapping("/generateExceptionWithAnnotatedStatusResource")
    fun getWithExceptionAndAnnotatedStatusResource(): Unit = throw RuntimeException("Another message")

    @GetMapping("/generateAnnotatedException")
    fun getWithAnnotatedException(): Unit = throw TestAnnotatedException()

    @GetMapping("/generateAnnotatedExceptionWithMessage")
    fun getWithAnnotatedExceptionWithMessage(): Unit = throw TestAnnotatedException("Some exception message")

    @GetMapping("/objectNotFoundException")
    fun getWithTestObjectNotFoundException(): Unit = throw TestObjectNotFoundException()

    @GetMapping("/preconditionFailed")
    fun getWithPreconditionFailedException(): Unit = throw object : ErrorResponseException(
        ErrorResponse(
            errorCode = "PRECONDITION_FAILED",
            attributes = mapOf("attribute" to "attribute"),
            messages = listOf("some message")), PRECONDITION_FAILED
    ) {}

    @GetMapping("/conflict")
    fun getWithConflictException(): Unit = throw object : ErrorResponseException(
        ErrorResponse(
            errorCode = "CONFLICT",
            attributes = mapOf("attribute" to "attribute"),
            messages = listOf("some message")), CONFLICT
    ) {}

    @GetMapping("/generateResponseStatusException")
    fun getWithResponseStatusException(): Unit =
        throw ResponseStatusException(INSUFFICIENT_STORAGE, "Insufficient storage")

    @GetMapping("/internalServerError")
    fun getWithDatabaseResponseException(): TestResponse {
        throw ResponseStatusException(INTERNAL_SERVER_ERROR, "Server error")
    }

    @GetMapping("/generateResponseStatusExceptionNoMessage")
    fun getWithResponseStatusExceptionNoMessage(): Unit =
        throw ResponseStatusException(INSUFFICIENT_STORAGE)

    @GetMapping("/generateAnnotatedResponseStatusException")
    fun getWithAnnotatedResponseStatusException(): Unit =
        throw TestResponseStatusException()

    @GetMapping("/malformedResponse")
    fun getMalformedResponse(@RequestParam responseCode: Int): ResponseEntity<String> =
        ResponseEntity.status(responseCode).body("blah")

    @GetMapping("/emptyResponse")
    fun getEmptyResponse(@RequestParam responseCode: Int): ResponseEntity<String> =
        ResponseEntity.status(responseCode).build()
}
