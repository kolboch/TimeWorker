package com.example.kb.worktimer.model

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

    init {
        observableInterval.doOnSubscribe {
            subscribersMonitor++
        }
    }

    fun startTimer() {
        Log.v("TIMER", "Trying to StartTimer, isRunning: $isRunning")
        if (isRunning && subscriber?.isDisposed == false) {
            return
        }
        subscriber = observableInterval.subscribe {
            currentTimeSeconds += 1
            update()
        }
        animateCallbackUI?.invoke(isRunning)
        isRunning = true
        acquireWakeLockCallback?.invoke()
    }

    fun stopTimer() {
        if (!isRunning) {
            return
        }
        animateCallbackUI?.invoke(isRunning)
        isRunning = false
        subscriber?.dispose()
        releaseWakeLockCallback?.invoke()
    }

    fun changeRunningState(onStopCallback: () -> Unit) {
        if (isRunning) {
            stopTimer()
            onStopCallback.invoke()
        } else {
            startTimer()
        }
    }

    fun setCurrentTimeAndUpdate(timeSeconds: Long) {
        currentTimeSeconds = timeSeconds
        update()
    }

    private fun update() {
        callbackNotification?.invoke(currentTimeSeconds)
        callbackUI?.invoke(currentTimeSeconds)
    }
}