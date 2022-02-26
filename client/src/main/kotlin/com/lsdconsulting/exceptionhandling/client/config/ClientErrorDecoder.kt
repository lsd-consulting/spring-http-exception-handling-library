package com.lsdconsulting.exceptionhandling.client.config

import com.lsdconsulting.exceptionhandling.client.exception.*
import com.lsdconsulting.exceptionhandling.client.exception.ErrorResponseException.Companion.create
import feign.Response
import feign.codec.ErrorDecoder
import org.apache.commons.io.IOUtils
import org.springframework.http.HttpStatus.*
import java.util.function.Function

class ClientErrorDecoder : ErrorDecoder {

    private val exceptionMap = mapOf(
        BAD_REQUEST.value() to Function { x: String -> create(BadRequestException::class.java, x) },
        NOT_FOUND.value() to Function { x: String -> create(NotFoundException::class.java, x) },
        CONFLICT.value() to Function { x: String -> create(ConflictException::class.java, x) },
        PRECONDITION_FAILED.value() to Function { x: String -> create(PreconditionFailedException::class.java, x) },
        INTERNAL_SERVER_ERROR.value() to Function { x: String -> create(InternalServerException::class.java, x) },
        NOT_IMPLEMENTED.value() to Function { x: String -> create(NotImplementedException::class.java, x) },
        BAD_GATEWAY.value() to Function { x: String -> create(BadGatewayException::class.java, x) },
        SERVICE_UNAVAILABLE.value() to Function { x: String -> create(ServiceUnavailableException::class.java, x) },
        GATEWAY_TIMEOUT.value() to Function { x: String -> create(GatewayTimeoutException::class.java, x) }
    )

    override fun decode(methodKey: String, response: Response): Exception? {
        val status = response.status()
        val responseBody = getResponseBody(response)
        log().error("Service returned status:{}, content:{}", status, responseBody)
        return getException(status, responseBody)
    }

    private fun getResponseBody(response: Response) =
        response.body()?.let { IOUtils.toString(it.asInputStream()) } ?: EMPTY_RESPONSE_BODY

    private fun getException(status: Int, responseBody: String): Exception? =
        exceptionMap[status]?.apply(responseBody) ?: create(InternalServerException::class.java, responseBody)

    companion object {
        private const val EMPTY_RESPONSE_BODY: String = ""
    }
}
