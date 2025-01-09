package com.lsdconsulting.exceptionhandling.server.config.attribute

import io.mockk.every
import io.mockk.mockk
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

import org.springframework.web.context.request.WebRequest

internal class UnknownErrorHandlerShould {

    private val attributePopulator = mockk<AttributePopulator>()
    private val request = mockk<WebRequest>()

    private val underTest = UnknownErrorHandler(attributePopulator)

    @Test
    internal fun `handle exception without message`() {
        every { attributePopulator.populateAttributes(any(), any())} returns mapOf()

        val result = underTest.handle(Exception(), request)

        assertThat(result.messages, hasSize(0))
    }

    @Test
    internal fun `handle exception with message`() {
        every { attributePopulator.populateAttributes(any(), any())} returns mapOf()

        val result = underTest.handle(Exception("message"), request)

        assertThat(result.messages, hasSize(1))
    }
}
