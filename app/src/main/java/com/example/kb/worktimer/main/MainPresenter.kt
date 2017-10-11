package com.example.kb.worktimer.main

import com.example.kb.worktimer.model.TimeFormatter
import com.example.kb.worktimer.model.Timer

/**
 * Created by Karlo on 2017-10-01.
 */
class MainPresenter(private val view: MainView) {

    private val LOG_TAG = "MainPresenter"

    private fun updateActivityTimer(seconds: Long) {
        val formatted = TimeFormatter.getTimeFromSeconds(seconds)
        view.onTimerUpdate(formatted)
    }

    fun onActivityToTimerBind() {
        Timer.callbackUI = { it -> updateActivityTimer(it) }
    }

    fun startStopTimer() {
        Timer.changeRunningState()
    }

    fun setUpTimer() {

    }
}