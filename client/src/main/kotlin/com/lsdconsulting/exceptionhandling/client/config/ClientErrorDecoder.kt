package com.lsdconsulting.exceptionhandling.client.config

import com.lsdconsulting.exceptionhandling.client.exception.*
import com.lsdconsulting.exceptionhandling.client.exception.ErrorResponseException.Companion.create
import feign.Response
import feign.codec.ErrorDecoder
import lsd.logging.log
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import java.io.IOException
import java.util.*

open class ClientErrorDecoder : ErrorDecoder {
    private val exceptionMap: EnumMap<HttpStatus, (String) -> ErrorResponseException?> =
        EnumMap(
            mapOf(
                BAD_REQUEST to { responseBody: String -> create(BadRequestException::class.java, responseBody) },
                NOT_FOUND to { responseBody: String -> create(NotFoundException::class.java, responseBody) },
                CONFLICT to { responseBody: String -> create(ConflictException::class.java, responseBody) },
                PRECONDITION_FAILED to { responseBody: String -> create(PreconditionFailedException::class.java, responseBody) },
                INTERNAL_SERVER_ERROR to { responseBody: String -> create(InternalServerException::class.java, responseBody) },
                NOT_IMPLEMENTED to { responseBody: String -> create(NotImplementedException::class.java, responseBody) },
                BAD_GATEWAY to { responseBody: String -> create(BadGatewayException::class.java, responseBody) },
                SERVICE_UNAVAILABLE to { responseBody: String -> create(ServiceUnavailableException::class.java, responseBody) },
                GATEWAY_TIMEOUT to { responseBody: String -> create(GatewayTimeoutException::class.java, responseBody) }
            )
        )

    override fun decode(methodKey: String, response: Response): Exception {
        val status = response.status()
        val responseBody = getResponseBody(response)
        log().error("Service returned status:{}, content:{}", status, responseBody)
        val exception = getException(status, responseBody)
        log().debug("Generated exception:{}", exception.message, exception)
        return exception
    }

    private fun getException(status: Int, responseBody: String): Exception {
        val httpStatus = resolve(status)
        return if (exceptionMap.containsKey(httpStatus)) exceptionMap[httpStatus]!!.invoke(responseBody) as Exception
        else create(exception = InternalServerException::class.java, responseBody = responseBody)!!
    }

    private fun getResponseBody(response: Response) = response.body()?.let { body: Response.Body ->
        return@getResponseBody try {
            body.asInputStream().bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            log().error(e.message)
            return ""
        }
    } ?: ""
}
