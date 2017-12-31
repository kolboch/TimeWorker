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
        set(callback) {
            field = callback
            update()
        }
    var updateNotificationUI: ((Long) -> Unit)? = null
        set(callback) {
            field = callback
            update()
        }
    var animateCallbackUI: ((Boolean) -> Unit)? = null
    var saveTimerState: ((Long, Long) -> Unit)? = null

    var measureDate: Long = -1L
        private set
    var currentTimeSeconds = 0L
        private set

    private var isRunning = false
    private var observableInterval = Observable.interval(1, TimeUnit.SECONDS)
    private var subscriber: Disposable? = null
    private var subscribersMonitor = 0

    init {
        observableInterval.doOnSubscribe {
            subscribersMonitor++
        }
    }

    fun startTimer(measureDate: Long) {
        Log.v("Timer", "Starting timer, isRunning: $isRunning")
        if (this.measureDate != -1L && this.measureDate != measureDate) {
            saveTimerState?.invoke(currentTimeSeconds, this.measureDate)
            setCurrentTimeAndUpdate(0, measureDate)
        }
        if (isRunning && subscriber?.isDisposed == false) {
            return
        }
        startUpdates()
        animateCallbackUI?.invoke(isRunning)
        isRunning = true
    }

    fun stopTimer() {
        if (!isRunning) {
            return
        }
        stopUpdates()
        animateCallbackUI?.invoke(isRunning)
        isRunning = false
        saveTimerState?.invoke(currentTimeSeconds, measureDate)
    }

    fun changeRunningState(measureDate: Long) {
        if (isRunning) {
            Log.v("Timer", "changeRunningState stopping")
            stopTimer()
        } else {
            Log.v("Timer", "changeRunningState starting")
            startTimer(measureDate)
        }
    }

    fun setCurrentTimeAndUpdate(timeSeconds: Long, measureDate: Long) {
        Log.v("Timer", "setCurrentTimeAndUpdate, seconds state $currentTimeSeconds")
        Log.v("Timer", "setCurrentTimeAndUpdate, passed seconds state $timeSeconds")
        this.measureDate = measureDate
        currentTimeSeconds = timeSeconds
        update()
    }

    fun startUpdates() {
        //TODO check if we can subscribe !!!
        subscriber = observableInterval.subscribe {
            //TODO compute difference
            update()
        }
    }

    fun stopUpdates() {
        subscriber?.dispose()
    }

    private fun update() {
        Log.v("Timer", "update called, seconds state $currentTimeSeconds")
        updateNotificationUI?.invoke(currentTimeSeconds)
        updateActivityUI?.invoke(currentTimeSeconds)
    }
}