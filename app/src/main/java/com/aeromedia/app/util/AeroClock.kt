package com.aeromedia.app.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Formatting for the Home screen's clock band — split into pieces
 *  (time / AM-PM / date) since the layout shows them as separate text runs
 *  at different sizes, matching the reference screenshot's layout. */
object AeroClock {
    private fun timeFormatter() = SimpleDateFormat("h:mm", Locale.getDefault())
    private fun ampmFormatter() = SimpleDateFormat("a", Locale.getDefault())
    private fun dateFormatter() = SimpleDateFormat("M/d/yy", Locale.getDefault())

    fun formatTimeOnly(date: Date): String = timeFormatter().format(date)
    fun formatAmPm(date: Date): String = ampmFormatter().format(date).uppercase(Locale.getDefault())
    fun formatShortDate(date: Date): String = dateFormatter().format(date)
}
