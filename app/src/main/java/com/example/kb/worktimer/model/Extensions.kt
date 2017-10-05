package com.example.kb.worktimer.model

import java.util.*

/**
 * Created by Karol on 2017-10-05.
 */

fun Calendar.getTodayDateInMillis(): Long {
    set(Calendar.HOUR, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    return this.timeInMillis
}