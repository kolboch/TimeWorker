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

    fun startStop(chronoTimeBase: Long, startStopCallback: (Boolean, Long) -> Unit) {
        startStopCallback(isWorking, getBase())
        if (isWorking) {
            stop(chronoTimeBase)
        } else {
            start()
        }

    }

    fun getChronoTimeBaseAndSetup(workTime: Long): Long {
        currentWorkTime = workTime
        return getBase()
    }

    @VisibleForTesting
    fun getBase(): Long {
        return SystemClock.elapsedRealtime() - currentWorkTime
    }

    @VisibleForTesting
    fun start() {
        isWorking = true
    }

    @VisibleForTesting
    fun stop(timeBase: Long) {
        currentWorkTime = SystemClock.elapsedRealtime() - timeBase
        isWorking = false
    }
}