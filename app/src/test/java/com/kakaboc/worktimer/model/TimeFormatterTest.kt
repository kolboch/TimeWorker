package com.kakaboc.worktimer.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Created by Karol on 2017-10-10.
 */
class TimeFormatterTest {

    private val hour = TimeUnit.HOURS.toSeconds(1)
    private val minute = TimeUnit.MINUTES.toSeconds(1)

    @Test
    fun whenSingleNumbersThenZerosDisplayed() {
        val result = TimeFormatter.getTimeFromSeconds(hour * 3 + minute * 7 + 9)
        assertEquals("03:07:09", result)
    }

    @Test
    fun whenNegativeThenZerosDisplayed() {
        val result = TimeFormatter.getTimeFromSeconds(-1)
        assertEquals("00:00:00", result)
    }

    @Test
    fun whenTwoNumbersThenNoPrefixZerosDisplay() {
        val result = TimeFormatter.getTimeFromSeconds(hour * 11 + minute * 26 + 55)
        assertEquals("11:26:55", result)
    }

}