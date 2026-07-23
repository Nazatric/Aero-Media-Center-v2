package com.aeromedia.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.Locale

class AeroClockTest {
    private fun dateAt(hour: Int, minute: Int): java.util.Date {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        return calendar.time
    }

    @Test
    fun `formatTimeOnly renders 12-hour time without leading zero`() {
        val result = AeroClock.formatTimeOnly(dateAt(16, 51))
        assertEquals("4:51", result)
    }

    @Test
    fun `formatAmPm is uppercase`() {
        val result = AeroClock.formatAmPm(dateAt(16, 51))
        assertTrue(result == "PM" || result == "AM")
    }

    @Test
    fun `formatShortDate contains slashes`() {
        val result = AeroClock.formatShortDate(dateAt(12, 0))
        assertTrue(result.contains("/"))
    }
}
