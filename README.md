[![semantic-release](https://img.shields.io/badge/semantic-release-e10079.svg?logo=semantic-release)](https://github.com/semantic-release/semantic-release)

# Spring Http Exception Handling Library

![GitHub](https://img.shields.io/github/license/lsd-consulting/spring-http-exception-handling-library)
![Codecov](https://img.shields.io/codecov/c/github/lsd-consulting/spring-http-exception-handling-library)

[![CI](https://github.com/lsd-consulting/spring-http-exception-handling-library/actions/workflows/ci.yml/badge.svg)](https://github.com/lsd-consulting/spring-http-exception-handling-library/actions/workflows/ci.yml)
[![Nightly Build](https://github.com/lsd-consulting/spring-http-exception-handling-library/actions/workflows/nightly.yml/badge.svg)](https://github.com/lsd-consulting/spring-http-exception-handling-library/actions/workflows/nightly.yml)
[![GitHub release](https://img.shields.io/github/release/lsd-consulting/spring-http-exception-handling-library)](https://github.com/lsd-consulting/spring-http-exception-handling-library/releases)
![Maven Central](https://img.shields.io/maven-central/v/io.github.lsd-consulting/spring-http-exception-handling-library-api)

## TODO
* add "failedAt" to the list of attributes
* document the `rest.request.receivedAt` request attribute
* document the `feign.retry.period`, `feign.retry.maxPeriod`, `feign.retry.maxAttempts` patameters with defaults
* add test for CustomResponseEntityExceptionHandler#handleBindException

This library's purpose is to standardise all HTTP related exception handling. It achieves this by declaring a common error response format, eg:

```json
{
  "errorCode": "SOME_ERROR_CODE",
  "messages": [
    "Validation failed"
  ],
  "dataErrors": [
    {
      "code": "Min",
      "name": "number",
      "value": "3",
      "message": "validation.lessThanAcceptedMinimum"
    },
    {
      "code": "Pattern",
      "name": "message",
      "value": "b",
      "message": "validation.wrongPattern"
    }
  ],
  "attributes": {
    "traceId": "40e1488ed1101adc",
    "exception": "MethodArgumentNotValidException",
    "startTime": "2022-02-31T10:00:00.000000Z"
  }
}
```

The library consists of two parts that can be used independently:
* The server side exception handling
* The client side exception handling
A service may use both or either library without the other.

The third part, the api, is what the other two depend on.

## Server library

The server library declares a number of exception handling configurations that support the following:
* Mapping of any exception extending the unchecked [ErrorResponseException](server/src/main/kotlin/com/lsdconsulting/exceptionhandling/server/exception/ErrorResponseException.kt).
* Mapping of a number of validation failure triggered exceptions to the same standardised response format.
* Mapping of any other exception thrown by the server side to the same standardised response format.
* @ResponseStatus annotated (non-ErrorResponseException) exceptions

It also provides server-side non-checked exceptions for some common cases, like a duplicate request.

### Usage

To use the server side all that is needed is to import the library, eg:
```groovy
implementation 'io.github.lsd-consulting:spring-http-exception-handling-library-api:+'
implementation 'io.github.lsd-consulting:spring-http-exception-handling-library-server:+'
```

#### Throwing exceptions
Make sure, that any exceptions the controllers throw or not catch extends the `ErrorResponseException` exception, eg:
```kotlin
import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import com.lsdconsulting.exceptionhandling.server.exception.ErrorResponseException
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

class TestException : ErrorResponseException(
    ErrorResponse(messages = listOf("Exception message"), errorCode = "ERROR_CODE"), INTERNAL_SERVER_ERROR
)
```
or are marked with the `@ResponseStatus` annotation, eg:
```kotlin
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(CONFLICT)
class TestAnnotatedException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor()
}
```

## Client library

The client library's main functions are:
* It declares the API classes - it's the classes that get serialised to the HTTP response body with specific fields and values.
* It configures a Feign error decoder - a class that knows how to deserialise a JSON error response into an exception
* It implements checked exception classes that correspond to the HTTP error codes (4xx & 5xx)

### Usage

Import the client project:
```groovy
implementation 'io.github.lsd-consulting:spring-http-exception-handling-library-api:+'
implementation 'io.github.lsd-consulting:spring-http-exception-handling-library-client:+'
```

Add the [ClientConfiguration](client/src/main/kotlin/com/lsdconsulting/exceptionhandling/client/config/ClientConfiguration.kt) to the Feign client, eg:

```kotlin
@FeignClient(name = "myService", url = "\${my.service.url}", configuration = [ClientConfiguration::class])
interface MyClient {

    @PostMapping("/objects")
    @Throws(ErrorResponseException::class)
    fun postObject(testRequest: TestRequest): TestResponse
}
```

NOTE
Please note that the exceptions thrown by the `ClientErrorDecoder` declared in the `ClientConfiguration` are checked exceptions,
so it's vital to make sure every method in the client declaring it throws at least the `ErrorResponseException` exception
as these should be handled by the client service and not allowed to leak to that service's API.

NOTE
If the `ClientErrorDecoder` encounters problems with handling the response body, it will throw an exception
of the corresponding type to the response code with all the fields empty except for the message: "Error message unavailable"  
 

## Api library

This library declares the API classes - it's the classes that get serialised to the HTTP response body with specific fields and values.
