package com.lsdconsulting.exceptionhandling.server.filter

import com.lsdconsulting.exceptionhandling.server.time.TimeProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.ServletException
import org.junit.jupiter.api.Test
import java.io.IOException
import java.time.ZonedDateTime

internal class RequestStartTimeFilterShould {

    @Test
    @Throws(ServletException::class, IOException::class)
    internal fun `use time provider`() {
        val timeProvider = mockk<TimeProvider>()
        val underTest = RequestStartTimeFilter(timeProvider)
        val now = ZonedDateTime.now()
        every { timeProvider.get() } returns now

        underTest.doFilterInternal(mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true))

        verify { timeProvider.get() }
    }
}
