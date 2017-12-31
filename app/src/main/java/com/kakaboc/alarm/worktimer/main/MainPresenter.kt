package com.kakaboc.alarm.worktimer.main

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import com.kakaboc.alarm.worktimer.R
import com.kakaboc.alarm.worktimer.database.MySqlHelper
import com.kakaboc.alarm.worktimer.model.TimeFormatter
import com.kakaboc.alarm.worktimer.model.MyTimer
import com.kakaboc.alarm.worktimer.services.TIMER_IS_WORKING
import com.kakaboc.alarm.worktimer.services.WorkTimeService

/**
 * Created by Karlo on 2017-10-01.
 */
class MainPresenter(private val view: MainView, private val context: Context) {

    private val LOG_TAG = "MainPresenter"
    private val dbHelper: MySqlHelper = MySqlHelper.getInstance(context)
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val workTimeServiceIntent = Intent(context, WorkTimeService::class.java)

    private fun updateActivityTimer(seconds: Long) {
        val formatted = TimeFormatter.getTimeFromSeconds(seconds)
        view.onTimerUpdate(formatted)
    }

    fun onActivityToTimerBind() {
        MyTimer.updateActivityUI = { it -> updateActivityTimer(it) }
        MyTimer.animateCallbackUI = { animateView(it) }
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
        MyTimer.changeRunningState(dbHelper.getDayTimeInMillis())
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

    fun onServiceRequested() {
        Log.v(LOG_TAG, "onServiceRequested")
        workTimeServiceIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startService(workTimeServiceIntent)
    }

    private fun updateWorkingTime() {
        if (MyTimer.measureDate != -1L) {
            dbHelper.updateWorkingTime(MyTimer.currentTimeSeconds, MyTimer.measureDate)
        }
    }

    fun onStatisticsActivityClicked() {
        updateWorkingTime()
    }
}