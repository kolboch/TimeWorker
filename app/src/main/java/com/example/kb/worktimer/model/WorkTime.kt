package com.example.kb.worktimer.model

import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Karlo on 2017-09-30.
 */
data class WorkTime(
        val date: Long,
        val timeWorked: Long
) {
    override fun toString(): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeWorked) % 60
        val hours = TimeUnit.MILLISECONDS.toHours(timeWorked)
        return "${Date(date)} $hours:$minutes"
    }
}