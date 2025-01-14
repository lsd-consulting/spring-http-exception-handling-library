package com.lsdconsulting.exceptionhandling.server.config

import com.lsdconsulting.exceptionhandling.api.DataError
import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import com.lsdconsulting.exceptionhandling.server.config.attribute.AttributePopulator
import com.lsdconsulting.exceptionhandling.server.config.attribute.UnknownErrorHandler
import lsd.logging.log
import org.springframework.beans.TypeMismatchException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.context.request.WebRequest.SCOPE_REQUEST
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.util.WebUtils.ERROR_EXCEPTION_ATTRIBUTE

/*
  The exceptions we are not handling here (explicitly):
    HttpRequestMethodNotSupportedException.class,
    HttpMediaTypeNotSupportedException.class,
    HttpMediaTypeNotAcceptableException.class,
    MissingPathVariableException.class,
    ServletRequestBindingException.class,
    ConversionNotSupportedException.class,
    HttpMessageNotWritableException.class,
    MissingServletRequestPartException.class,
    NoHandlerFoundException.class,
    AsyncRequestTimeoutException.class
*/
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 11) // not sure why but this should be higher than ControllerExceptionHandler
class CustomResponseEntityExceptionHandler(
    private val attributePopulator: AttributePopulator,
    private val unknownErrorHandler: UnknownErrorHandler
) : ResponseEntityExceptionHandler() {

    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val dataError = DataError(
            message = "validation.missingRequestParameter",
            name = ex.parameterName,
            code = ex.parameterType,
            value = null
        )
        val errorResponse = ErrorResponse(
            errorCode = INVALID_ERROR_CODE,
            messages = listOf(VALIDATION_FAILED_MESSAGE),
            dataErrors = listOf(dataError),
            attributes = attributePopulator.populateAttributes(ex, request))
        log().error("Handling MissingServletRequestParameterException - httpStatus:{}, errorResponse:{}", status, errorResponse)
        return ResponseEntity(errorResponse, status)
    }

    @Suppress("removal", "OVERRIDE_DEPRECATION")
    override fun handleBindException(
        ex: BindException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errorResponse = ErrorResponse(
            errorCode = INVALID_ERROR_CODE,
            messages = listOf(DATA_MISSING_MESSAGE),
            dataErrors = dataErrorsFromBindingResults(ex.bindingResult),
            attributes = attributePopulator.populateAttributes(ex, request))
        log().error("Handling BindException - httpStatus:{}, errorResponse:{}", status, errorResponse)
        return ResponseEntity(errorResponse, status)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errorResponse = ErrorResponse(
            errorCode = INVALID_ERROR_CODE,
            messages = listOf(VALIDATION_FAILED_MESSAGE),
            dataErrors = dataErrorsFromBindingResults(ex.bindingResult),
            attributes = attributePopulator.populateAttributes(ex, request))
        log().error("Handling MethodArgumentNotValidException - httpStatus:{}, errorResponse:{}", status, errorResponse)
        return ResponseEntity(errorResponse, status)
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errorResponse = ErrorResponse(
            errorCode = MALFORMED_ERROR_CODE,
            messages = listOf(MESSAGE_PARSE_ERROR_MESSAGE),
            attributes = attributePopulator.populateAttributes(ex, request))
        log().error("Handling HttpMessageNotReadableException - httpStatus:{}, errorResponse:{}", status, errorResponse)
        return ResponseEntity(errorResponse, status)
    }

    override fun handleTypeMismatch(
        ex: TypeMismatchException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val dataError = DataError(
            name = ex.propertyName,
            value = ex.value?.toString(),
            message = ex.message,
            code = ex.errorCode)
        val errorResponse = ErrorResponse(
            errorCode = MALFORMED_ERROR_CODE,
            messages = listOf(DATA_TYPE_ERROR_MESSAGE),
            dataErrors = listOf(dataError),
            attributes = attributePopulator.populateAttributes(ex, request))
        log().error("Handling TypeMismatchException - httpStatus:{}, errorResponse:{}", status, errorResponse)
        return ResponseEntity(errorResponse, status)
    }

    // Default handler for all the exceptions not explicitly handled by this class
    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        if (INTERNAL_SERVER_ERROR == status) {
            request.setAttribute(ERROR_EXCEPTION_ATTRIBUTE, ex, SCOPE_REQUEST)
        }
        val errorResponse = unknownErrorHandler.handle(ex, request)
        log().error("Handling unknown exception - httpStatus:{}, errorResponse:{}", status, errorResponse)
        return ResponseEntity(errorResponse, status)
    }

    private fun dataErrorsFromBindingResults(bindingResult: BindingResult): List<DataError> = bindingResult
        .fieldErrors
        .map(this::convert)
        .sortedWith(Comparator.comparing { obj: DataError -> obj.name + obj.value + obj.code!! })

    private fun convert(fieldError: FieldError) = DataError(
        message = fieldError.defaultMessage,
        name = fieldError.field,
        code = fieldError.code,
        value = fieldError.rejectedValue?.toString())

    companion object {
        private const val INVALID_ERROR_CODE = "INVALID"
        private const val VALIDATION_FAILED_MESSAGE = "Validation failed"
        private const val DATA_MISSING_MESSAGE = "Data missing"
        private const val MALFORMED_ERROR_CODE = "MALFORMED"
        private const val MESSAGE_PARSE_ERROR_MESSAGE = "Message not readable"
        private const val DATA_TYPE_ERROR_MESSAGE = "Data type error"
    }
}
