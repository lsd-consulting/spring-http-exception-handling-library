package com.lsdconsulting.exceptionhandling.server.testapp.api.exception

import org.springframework.http.HttpStatus.INSUFFICIENT_STORAGE
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(INSUFFICIENT_STORAGE)
class TestResponseStatusException : Exception("Insufficient storage")
