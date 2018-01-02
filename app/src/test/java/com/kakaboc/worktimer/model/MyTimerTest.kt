package com.kakaboc.worktimer.model

import org.junit.Test

import org.junit.Assert.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Karlo on 2018-01-02.
 */
class MyTimerTest {

    private val calendar = Calendar.getInstance()
    private val hour = TimeUnit.HOURS.toSeconds(1)
    private val minute = TimeUnit.MINUTES.toSeconds(1)

    @Test
    fun computeStartingTime() {
        calendar.set(2018, 1, 23, 17, 25, 10) // 23.01.2018 17:25:10
        val startingTime = MyTimer.computeStartingTime(calendar.timeInMillis, hour * 2 + 23 * minute + 14) // working time 2:23:14
        calendar.set(2018, 1, 23, 15, 1, 56) // after subtracting 23.01.2018 15:01:56
        assertEquals(calendar.timeInMillis, startingTime)
    }

}