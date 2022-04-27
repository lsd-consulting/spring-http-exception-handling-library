package com.lsdconsulting.exceptionhandling.server.config

import com.google.common.collect.Iterables
import com.lsdconsulting.exceptionhandling.api.DataError
import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import com.lsdconsulting.exceptionhandling.server.config.attribute.AttributePopulator
import com.lsdconsulting.exceptionhandling.server.config.attribute.UnknownErrorHandler
import com.lsdconsulting.exceptionhandling.server.exception.ErrorResponseException
import org.springframework.core.Ordered
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
import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 10) // not sure why but this should be lower than CustomResponseEntityExceptionHandler
class ControllerExceptionHandler(
    private val unknownErrorHandler: UnknownErrorHandler,
    private val attributePopulator: AttributePopulator
) {

    @ExceptionHandler(Exception::class)
    @ResponseBody
    protected fun handleUncaughtException(ex: Exception, request: WebRequest): ResponseEntity<*> {
        return when (ex) {
            is ConstraintViolationException -> handle(ex, request)
            is ResponseStatusException -> handle(ex, request)
            is ErrorResponseException -> handle(ex, request)
            else -> ResponseEntity(unknownErrorHandler.handle(ex, request), getResponseStatusFromAnnotation(ex))
        }
    }

    private fun handle(ex: ErrorResponseException, request: WebRequest): ResponseEntity<*> {
        val errorResponse = ex.errorResponse.copy(attributes = ex.errorResponse.attributes + attributePopulator.populateAttributes(ex, request))
        return ResponseEntity(errorResponse, ex.httpStatus)
    }

    private fun handle(ex: ResponseStatusException, request: WebRequest): ResponseEntity<*> {
        val errorResponse = ErrorResponse(
            errorCode = ex.status.name,
//            messages = if (ex.message != null) listOf(ex.message!!) else listOf(),
            messages = listOf(ex.message!!),
            attributes = attributePopulator.populateAttributes(ex, request)
        )
        return ResponseEntity(errorResponse, ex.status)
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

    private fun convert(constraintViolation: ConstraintViolation<*>): DataError {
        return DataError(
            message = constraintViolation.message,
            value = constraintViolation.invalidValue.toString(),
            name = Iterables.getLast(constraintViolation.propertyPath).name,
            code = constraintViolation.constraintDescriptor.annotation.annotationClass.simpleName)
    }

    companion object {
        private const val PARAMETER_VALIDATION_FAILED_ERROR_CODE = "INVALID_PARAMETER"
        private const val VALIDATION_FAILED = "Validation failed"
    }
}
