package com.lsdconsulting.exceptionhandling.server.matcher

import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.hamcrest.Description
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.Is.`is`

class ErrorDetailResponseMatcher(private val expectedErrorResponse: ErrorResponse) : TypeSafeMatcher<ErrorResponse>() {

    override fun matchesSafely(errorResponse: ErrorResponse): Boolean {
        assertThat(errorResponse.errorCode, `is`(expectedErrorResponse.errorCode))
        assertThat(errorResponse.attributes.entries, everyItem(`in`(expectedErrorResponse.attributes.entries)))
        assertThat(expectedErrorResponse.attributes.entries, everyItem(`in`(errorResponse.attributes.entries)))
        assertThat(errorResponse.dataErrors, containsInAnyOrder<Any>(*expectedErrorResponse.dataErrors.toTypedArray()))
        assertThat(errorResponse.messages, containsInAnyOrder<Any>(*expectedErrorResponse.messages.toTypedArray()))
        return true
    }

    override fun describeTo(description: Description) {
        description.appendText("equal to $expectedErrorResponse")
    }

    companion object {
        fun equalTo(errorResponse: ErrorResponse) = ErrorDetailResponseMatcher(errorResponse)
    }
}
