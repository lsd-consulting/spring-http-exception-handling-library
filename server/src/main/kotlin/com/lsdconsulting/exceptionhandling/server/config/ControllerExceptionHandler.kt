package com.lsdconsulting.exceptionhandling.server.config

import com.lsdconsulting.exceptionhandling.api.DataError
import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import com.lsdconsulting.exceptionhandling.server.config.attribute.AttributePopulator
import com.lsdconsulting.exceptionhandling.server.config.attribute.UnknownErrorHandler
import com.lsdconsulting.exceptionhandling.server.exception.ErrorResponseException
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import lsd.logging.log
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
@Order(LOWEST_PRECEDENCE - 10) // not sure why but this should be lower than CustomResponseEntityExceptionHandler
class ControllerExceptionHandler(
    private val unknownErrorHandler: UnknownErrorHandler,
    private val attributePopulator: AttributePopulator
) {

    @ExceptionHandler(Exception::class)
    @ResponseBody
    protected fun handleUncaughtException(ex: Exception, request: WebRequest): ResponseEntity<*> {
        val responseEntity = when (ex) {
            is ConstraintViolationException -> handle(ex, request)
            is ResponseStatusException -> handle(ex, request)
            is ErrorResponseException -> handle(ex, request)
            else -> ResponseEntity(unknownErrorHandler.handle(ex, request), getResponseStatusFromAnnotation(ex))
        }
        log().error("For request:{}, generated httpStatus:{}, errorResponse:{}", request, responseEntity.statusCode, responseEntity.body)
        log().error("Handling exception", ex)
        return responseEntity
    }

    private fun handle(ex: ErrorResponseException, request: WebRequest): ResponseEntity<*> {
        val errorResponse = ex.errorResponse.copy(attributes = ex.errorResponse.attributes + attributePopulator.populateAttributes(ex, request))
        return ResponseEntity(errorResponse, ex.httpStatus)
    }

    private fun handle(ex: ResponseStatusException, request: WebRequest): ResponseEntity<*> {
        val errorResponse = ErrorResponse(
            errorCode = ex.statusCode.toString(),
            messages = listOf(ex.message ?: "Message missing"),
            attributes = attributePopulator.populateAttributes(ex, request)
        )
        return ResponseEntity(errorResponse, ex.statusCode)
    }

    private fun handle(ex: ConstraintViolationException, request: WebRequest): ResponseEntity<*> {
        val bodyDataErrors = ex.constraintViolations
            .map { constraintViolation: ConstraintViolation<*> -> convert(constraintViolation) }
            .sortedBy { it.name + it.value + it.code }
        val errorResponse = ErrorResponse(
            errorCode = PARAMETER_VALIDATION_FAILED_ERROR_CODE,
            messages = listOf(VALIDATION_FAILED),
            dataErrors = bodyDataErrors,
            attributes = attributePopulator.populateAttributes(ex, request)
        )
        return ResponseEntity(errorResponse, BAD_REQUEST)
    }

    private fun getResponseStatusFromAnnotation(ex: Exception): HttpStatus {
        // check if exception was annotated with @ResponseStatus
        val responseStatus = AnnotatedElementUtils.findMergedAnnotation(ex.javaClass, ResponseStatus::class.java)
        return responseStatus?.code ?: INTERNAL_SERVER_ERROR
    }

    private fun convert(constraintViolation: ConstraintViolation<*>) = DataError(
        message = constraintViolation.message,
        value = constraintViolation.invalidValue?.toString(),
        name = constraintViolation.propertyPath.last().name,
        code = constraintViolation.constraintDescriptor.annotation.annotationClass.simpleName)

    companion object {
        private const val PARAMETER_VALIDATION_FAILED_ERROR_CODE = "INVALID_PARAMETER"
        private const val VALIDATION_FAILED = "Validation failed"
    }
}
