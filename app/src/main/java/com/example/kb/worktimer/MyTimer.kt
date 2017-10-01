package com.example.kb.worktimer

import android.os.SystemClock
import android.util.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by Karlo on 2017-10-01.
 */
class MyTimer(var timeElapsed: Long = 0) {

    private var startTime: Long = System.nanoTime()
    private lateinit var observable: Observable<Long>

    fun startTimer() {
        startTime = System.nanoTime()
        observable = Observable.interval(1, TimeUnit.SECONDS)
        observable.subscribe {
            updateTimeElapsed()
            val timeInSeconds = TimeUnit.NANOSECONDS.toSeconds(timeElapsed)
            Log.v("MyTimer", "MyTimer logs time: $timeInSeconds")
        }
    }

    fun resetTimer() {
        timeElapsed = 0
    }

    fun stopTimer() {
        timeElapsed += System.nanoTime() - startTime
    }

    fun getCurrentTimeElapsed() = timeElapsed

    fun getCurrentTimeElapsedInSeconds() = TimeUnit.NANOSECONDS.toSeconds(timeElapsed)

    private fun updateTimeElapsed() {
        timeElapsed = System.nanoTime() - startTime
    }
}