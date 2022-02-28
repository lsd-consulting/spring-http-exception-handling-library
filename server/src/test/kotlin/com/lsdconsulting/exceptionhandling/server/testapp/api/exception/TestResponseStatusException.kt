package com.lsdconsulting.exceptionhandling.server.testapp.api.exception

import org.springframework.http.HttpStatus.INSUFFICIENT_STORAGE
import org.springframework.web.server.ResponseStatusException
import java.io.IOException

class TestResponseStatusException : ResponseStatusException(INSUFFICIENT_STORAGE, "Insufficient storage", IOException())
