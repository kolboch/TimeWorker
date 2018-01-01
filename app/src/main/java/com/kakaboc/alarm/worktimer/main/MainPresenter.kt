package com.kakaboc.alarm.worktimer.main

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import com.kakaboc.alarm.worktimer.R
import com.kakaboc.alarm.worktimer.database.MySqlHelper
import com.kakaboc.alarm.worktimer.model.MyTimer
import com.kakaboc.alarm.worktimer.model.MyTimer.computeStartingTime
import com.kakaboc.alarm.worktimer.model.TimeFormatter
import com.kakaboc.alarm.worktimer.services.TIMER_START_TIME
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

    private fun animateView(isRunning: Boolean) {
        if (isRunning) {
            view.onTimerStarted()
        } else {
            view.onTimerStopped()
        }
    }

    fun startStopTimer() {
        MyTimer.changeRunningState(computeStartingTime(dbHelper.getCurrentTimeMillis(), dbHelper.getTodayWorkingTime()), dbHelper.getDayTimeInMillis())
    }

    private fun refreshTimerState() {
        val isWorking = preferences.getLong(TIMER_START_TIME, -1L) != -1L
        if (isWorking) {
            view.onTimerButtonUpdate(R.string.stop)
        } else {
            view.onTimerButtonUpdate(R.string.start)
            view.onTimerUpdate(TimeFormatter.getTimeFromSeconds(dbHelper.getTodayWorkingTime()))
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