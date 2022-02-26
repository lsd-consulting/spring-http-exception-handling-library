package com.lsdconsulting.exceptionhandling.server.filter

import com.lsdconsulting.exceptionhandling.server.time.TimeProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.io.IOException
import java.time.ZonedDateTime
import javax.servlet.ServletException

internal class RequestStartTimeFilterShould {

    @Test
    @Throws(ServletException::class, IOException::class)
    fun useTimeProvider() {
        val timeProvider = mockk<TimeProvider>()
        val underTest = RequestStartTimeFilter(timeProvider)
        val now = ZonedDateTime.now()
        every { timeProvider.get() } returns now

        underTest.doFilterInternal(mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true))

        verify { timeProvider.get() }
    }
}
