package com.example.kb.worktimer.model

import android.os.SystemClock
import android.util.Log

/**
 * Created by Karol on 2017-10-04.
 */
class ChronometerMonitor(
        private var isWorking: Boolean = false,
        private var currentWorkTime: Long = 0
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

    private fun getBase(): Long {
        return SystemClock.elapsedRealtime() - currentWorkTime
    }

    private fun start() {
        isWorking = true
    }

    private fun stop(timeBase: Long) {
        currentWorkTime = SystemClock.elapsedRealtime() - timeBase
        Log.v("Chrono monitor", "$currentWorkTime")
        isWorking = false
    }
}