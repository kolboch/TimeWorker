package com.example.kb.worktimer.model

import java.util.concurrent.TimeUnit

/**
 * Created by Karol on 2017-10-10.
 */
object TimeFormatter {
    /**
     * returns String time representation in hh:mm:ss format
     */
    fun getTimeFromSeconds(seconds: Long): String {
        if (seconds < 0) {
            return "00:00:00"
        }
        val minutes = TimeUnit.SECONDS.toMinutes(seconds)
        val minutesOfHour = minutes % 60
        val hours = minutes / 60
        val secondsOfMinute = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutesOfHour, secondsOfMinute)
    }
}