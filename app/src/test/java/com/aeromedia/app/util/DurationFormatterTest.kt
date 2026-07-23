package com.aeromedia.app.util

import org.junit.Assert.assertEquals
import org.junit.Test

class DurationFormatterTest {
    @Test
    fun `zero duration formats as 0-00`() {
        assertEquals("0:00", DurationFormatter.format(0L))
    }

    @Test
    fun `under a minute formats as m-ss`() {
        assertEquals("0:58", DurationFormatter.format(58_000L))
    }

    @Test
    fun `several minutes formats correctly`() {
        assertEquals("4:58", DurationFormatter.format(298_000L))
    }

    @Test
    fun `an hour or more includes the hour segment`() {
        assertEquals("1:02:03", DurationFormatter.format((3600 + 120 + 3) * 1000L))
    }
}
