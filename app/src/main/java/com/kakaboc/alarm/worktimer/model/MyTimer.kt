package com.kakaboc.alarm.worktimer.model

import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit


/**
 * Created by Karlo on 2017-10-09.
 */
object MyTimer {

    var updateActivityUI: ((Long) -> Unit)? = null
    var updateNotificationUI: ((Long) -> Unit)? = null
    var animateCallbackUI: ((Boolean) -> Unit)? = null
    var saveTimerState: ((Long, Long) -> Unit)? = null
    var clearStartTime: (() -> Unit)? = null
    var setStartTime: ((Long) -> Unit)? = null
    var getCurrentTimeMillis: (() -> Long)? = null

    var measureDate: Long = -1L
        private set
    var currentTimeSeconds = 0L
        private set

    private var observableInterval = Observable.interval(1, TimeUnit.SECONDS)
    private var subscriber: Disposable? = null
    private var startTime: Long = -1L
    private var isRunning = startTime != -1L
        get() {
            return startTime != -1L
        }
    private val LOG_TAG = "Timer"

    fun startTimer(startingTime: Long, measureDate: Long) {
        Log.v(LOG_TAG, "Starting timer, isRunning: $isRunning")
        onNewMeasureDate(measureDate)
        if (isRunning && subscriber?.isDisposed == false) {
            return
        }
        setUpStartTime(startingTime)
        startUpdates()
        animateCallbackUI?.invoke(isRunning)
    }

    fun stopTimer() {
        if (!isRunning) {
            return
        }
        stopUpdates()
        currentTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(getCurrentTimeMillis!!.invoke() - startTime)
        saveTimerState?.invoke(currentTimeSeconds, measureDate)
        clearStartTime?.invoke()
        startTime = -1L
        animateCallbackUI?.invoke(isRunning)
    }

    fun changeRunningState(currentTimeMillis: Long, measureDate: Long) {
        if (isRunning) {
            Log.v(LOG_TAG, "changeRunningState stopping")
            stopTimer()
        } else {
            Log.v(LOG_TAG, "changeRunningState starting")
            startTimer(currentTimeMillis, measureDate)
        }
    }

    fun startUpdates() {
        val isDisposed = subscriber?.isDisposed ?: true
        if (isDisposed) {
            subscriber = observableInterval.subscribe {
                currentTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(getCurrentTimeMillis!!.invoke() - startTime)
                update()
            }
        }
    }

    fun stopUpdates() {
        val isDisposed = subscriber?.isDisposed ?: true
        if (!isDisposed) {
            subscriber?.dispose()
        }
    }

    fun setUpTimer(startTime: Long, measureDate: Long) {
        this.startTime = startTime
        onNewMeasureDate(measureDate)
    }

    fun computeStartingTime(currentTimeMillis: Long, workingTimeSeconds: Long): Long {
        return currentTimeMillis - TimeUnit.SECONDS.toMillis(workingTimeSeconds)
    }

    private fun update() {
        Log.v(LOG_TAG, "update called, seconds state $currentTimeSeconds")
        updateNotificationUI?.invoke(currentTimeSeconds)
        updateActivityUI?.invoke(currentTimeSeconds)
    }

    private fun setCurrentTimeAndUpdate(startTime: Long) {
        Log.v(LOG_TAG, "setCurrentTimeAndUpdate, seconds state $currentTimeSeconds")
        Log.v(LOG_TAG, "setCurrentTimeAndUpdate, new start time $startTime")
        setUpStartTime(startTime)
        update()
    }

    private fun setUpStartTime(startTime: Long) {
        this.startTime = startTime
        setStartTime?.invoke(startTime)
    }

    private fun onNewMeasureDate(measureDate: Long) {
        if (this.measureDate == -1L) {
            this.measureDate = measureDate
        } else if (this.measureDate != measureDate) {
            saveTimerState?.invoke(currentTimeSeconds, this.measureDate)
            this.measureDate = measureDate
            setCurrentTimeAndUpdate(0)
        }
    }

}