package com.kakaboc.alarm.worktimer.model

import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit


/**
 * Created by Karlo on 2017-10-09.
 */
object Timer {

    var isRunning = false
        private set
    var currentTimeSeconds = 0L
        private set
    private var observableInterval = Observable.interval(1, TimeUnit.SECONDS)
    private var subscriber: Disposable? = null
    private var subscribersMonitor = 0
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
    var acquireWakeLockCallback: (() -> Unit)? = null
    var releaseWakeLockCallback: (() -> Unit)? = null
    var scheduleAlarmManager: (() -> Unit)? = null
    var cancelAlarmManager: (() -> Unit)? = null
    var saveTimerState: ((Long) -> Unit)? = null

    init {
        observableInterval.doOnSubscribe {
            subscribersMonitor++
        }
    }

    fun startTimer() {
        Log.v("Timer", "Starting timer, isRunning: $isRunning")
        if (isRunning && subscriber?.isDisposed == false) {
            return
        }
        subscriber = observableInterval.subscribe {
            currentTimeSeconds += 1
            update()
        }
        animateCallbackUI?.invoke(isRunning)
        isRunning = true
        scheduleAlarmManager?.invoke()
        acquireWakeLockCallback?.invoke()
    }

    fun stopTimer() {
        if (!isRunning) {
            return
        }
        animateCallbackUI?.invoke(isRunning)
        isRunning = false
        saveTimerState?.invoke(currentTimeSeconds)
        subscriber?.dispose()
        cancelAlarmManager?.invoke()
        releaseWakeLockCallback?.invoke()
    }

    fun changeRunningState(onStopCallback: () -> Unit) {
        if (isRunning) {
            Log.v("Timer", "changeRunningState stopping")
            stopTimer()
            onStopCallback.invoke()
        } else {
            Log.v("Timer", "changeRunningState starting")
            startTimer()
        }
    }

    fun setCurrentTimeAndUpdate(timeSeconds: Long) {
        Log.v("Timer", "setCurrentTimeAndUpdate, seconds state $currentTimeSeconds")
        Log.v("Timer", "setCurrentTimeAndUpdate, passed seconds state $timeSeconds")
        currentTimeSeconds = timeSeconds
        update()
    }

    private fun update() {
        Log.v("Timer", "update called, seconds state $currentTimeSeconds")
        callbackNotification?.invoke(currentTimeSeconds)
        callbackUI?.invoke(currentTimeSeconds)
    }
}