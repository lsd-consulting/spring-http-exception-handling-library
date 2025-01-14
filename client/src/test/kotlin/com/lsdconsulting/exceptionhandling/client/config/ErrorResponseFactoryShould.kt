package com.lsdconsulting.exceptionhandling.client.config

import com.lsdconsulting.exceptionhandling.api.DataError
import com.lsdconsulting.exceptionhandling.api.ErrorResponse
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

internal class ErrorResponseFactoryShould {

    @Test
    @Throws(IOException::class)
    internal fun `convert json to error response`() {
        val result = ErrorResponseFactory.from(json = Files.readString(Paths.get("src/test/resources/data/errorResponse.json")))

        val expectedResult = ErrorResponse(
            errorCode = "ERROR_CODE",
            messages = listOf("message1", "message2"),
            dataErrors = listOf(
                DataError(code = "Code1", name = "name1", value = "a", message = "data_error_message1"),
                DataError(code = "Code2", name = "name2", value = "b", message = "data_error_message2")
            ),
            attributes = mapOf("attribute1" to "value1", "attribute2" to "value2")
        )

        assertThat(result, equalTo(expectedResult))
    }
}
