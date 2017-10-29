package com.kakaboc.alarm.worktimer.model

import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit


/**
 * Created by Karlo on 2017-10-09.
 */
object MyTimer {

    var isRunning = false
        private set
    var currentTimeSeconds = 0L
        private set
    var measureDate: Long = -1L

    var callbackUI: ((Long) -> Unit)? = null
        set(callback) {
            field = callback
            update()
        }
    var callbackNotification: ((Long) -> Unit)? = null
        set(callback) {
            field = callback
            update()
        }

    var animateCallbackUI: ((Boolean) -> Unit)? = null
    var saveTimerState: ((Long, Long) -> Unit)? = null

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
            saveTimerState?.invoke(currentTimeSeconds, measureDate)
            setCurrentTimeAndUpdate(0, measureDate)
        }
        if (isRunning && subscriber?.isDisposed == false) {
            return
        }
        subscriber = observableInterval.subscribe {
            currentTimeSeconds += 1
            update()
        }
        animateCallbackUI?.invoke(isRunning)
        isRunning = true
    }

    fun resumeTimer(timePassedSeconds: Long) {
        if (isRunning && subscriber?.isDisposed == false) {
            return
        }
        currentTimeSeconds += timePassedSeconds
        subscriber = observableInterval.subscribe {
            currentTimeSeconds += 1
            update()
        }
        animateCallbackUI?.invoke(isRunning)
        isRunning = true
    }

    fun stopTimer() {
        if (!isRunning) {
            return
        }
        animateCallbackUI?.invoke(isRunning)
        isRunning = false
        saveTimerState?.invoke(currentTimeSeconds, measureDate)
        subscriber?.dispose()
    }

    fun changeRunningState(onStopCallback: () -> Unit, measureDate: Long) {
        if (isRunning) {
            Log.v("Timer", "changeRunningState stopping")
            stopTimer()
            onStopCallback.invoke()
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

    private fun update() {
        Log.v("Timer", "update called, seconds state $currentTimeSeconds")
        callbackNotification?.invoke(currentTimeSeconds)
        callbackUI?.invoke(currentTimeSeconds)
    }
}