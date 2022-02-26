package com.lsdconsulting.exceptionhandling.server.testapp.api.exception

import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(CONFLICT)
class TestAnnotatedException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor()
}
