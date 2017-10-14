package com.example.kb.worktimer.main

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import com.example.kb.worktimer.R
import com.example.kb.worktimer.database.MySqlHelper
import com.example.kb.worktimer.model.TimeFormatter
import com.example.kb.worktimer.model.Timer
import com.example.kb.worktimer.services.TIMER_IS_WORKING
import com.example.kb.worktimer.services.WorkTimeService

/**
 * Created by Karlo on 2017-10-01.
 */
class MainPresenter(private val view: MainView, val context: Context) {

    private val LOG_TAG = "MainPresenter"
    private val dbHelper: MySqlHelper = MySqlHelper.getInstance(context)
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val workTimeServiceIntent = Intent(context, WorkTimeService::class.java)

    private fun updateActivityTimer(seconds: Long) {
        val formatted = TimeFormatter.getTimeFromSeconds(seconds)
        view.onTimerUpdate(formatted)
    }

    fun onActivityToTimerBind() {
        Timer.callbackUI = { it -> updateActivityTimer(it) }
        Timer.animateCallbackUI = { animateView(it) }
        refreshTimerState()
    }

    private fun animateView(wasRunning: Boolean) {
        if (wasRunning) {
            view.onTimerStopped()
        } else {
            view.onTimerStarted()
        }
    }

    fun startStopTimer() {
        Timer.changeRunningState()
    }

    private fun refreshTimerState() {
        view.onTimerUpdate(TimeFormatter.getTimeFromSeconds(dbHelper.getTodayWorkingTime()))
        val wasWorking = preferences.getBoolean(TIMER_IS_WORKING, false)
        if (wasWorking) {
            view.onTimerButtonUpdate(R.string.stop)
        } else {
            view.onTimerButtonUpdate(R.string.start)
        }
    }

    fun onActivityDestroyed() {
        Log.v(LOG_TAG, "Setting up timer state in presenter. ${Timer.isRunning}")
        dbHelper.updateTodayWorkingTime(Timer.currentTimeSeconds)
        preferences.edit().putBoolean(TIMER_IS_WORKING, Timer.isRunning).apply()
    }

    fun onServiceRequested() {
        workTimeServiceIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startService(workTimeServiceIntent)
    }
}