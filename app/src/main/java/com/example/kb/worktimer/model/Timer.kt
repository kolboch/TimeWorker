package com.example.kb.worktimer.model

import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by Karlo on 2017-10-09.
 */
object Timer {

    private var isRunning = false
    private var currentTimeSeconds = 0L
    private var observableInterval = Observable.interval(1, TimeUnit.SECONDS)
    private lateinit var subscriber: Disposable

    var callbackUI: ((Long) -> Unit)? = null
    var callbackNotification: ((Long) -> Unit)? = null

    fun stopTimer() {
        isRunning = false
        subscriber.dispose()
    }

    fun startTimer() {
        subscriber = observableInterval.observeOn(Schedulers.io()).subscribe {
            currentTimeSeconds += 1
            update()
            Log.v("Timer", "time elapsed: $currentTimeSeconds")
        }
        isRunning = true
    }

    fun setCurrentTime(timeSeconds: Long) {
        currentTimeSeconds = timeSeconds
    }

    private fun update() {
        callbackNotification?.invoke(currentTimeSeconds)
        callbackUI?.invoke(currentTimeSeconds)
    }
}