package com.example.kb.worktimer.model

import android.os.SystemClock
import android.support.annotation.VisibleForTesting

/**
 * Created by Karol on 2017-10-04.
 */
class ChronometerMonitor(
        @VisibleForTesting var isWorking: Boolean = false,
        @VisibleForTesting var currentWorkTime: Long = 0
) {

    fun startStop(chronoTimeBase: Long, startStopCallback: (Boolean, Long, Long) -> Unit) {
        val lastChronometerState = isWorking
        if (isWorking) {
            stopAndUpdateWorkingTime(chronoTimeBase)
        } else {
            start()
        }
        startStopCallback(lastChronometerState, getBase(), currentWorkTime)
    }

    fun getChronoTimeBaseAndSetup(workTime: Long): Long {
        currentWorkTime = workTime
        return getBase()
    }

    @VisibleForTesting
    private fun getBase(): Long {
        return SystemClock.elapsedRealtime() - currentWorkTime
    }

    @VisibleForTesting
    fun start() {
        isWorking = true
    }

    @VisibleForTesting
    fun stopAndUpdateWorkingTime(timeBase: Long) {
        currentWorkTime = SystemClock.elapsedRealtime() - timeBase
        isWorking = false
    }
}