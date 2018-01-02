package com.kakaboc.worktimer.main

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import com.kakaboc.worktimer.R
import com.kakaboc.worktimer.database.MySqlHelper
import com.kakaboc.worktimer.model.MyTimer
import com.kakaboc.worktimer.model.MyTimer.computeStartingTime
import com.kakaboc.worktimer.model.MyTimer.startUpdates
import com.kakaboc.worktimer.model.TimeFormatter
import com.kakaboc.worktimer.services.TIMER_START_TIME
import com.kakaboc.worktimer.services.WorkTimeService

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

    fun startStopTimer() {
        MyTimer.changeRunningState(computeStartingTime(dbHelper.getCurrentTimeMillis(), dbHelper.getTodayWorkingTime()), dbHelper.getDayTimeInMillis())
    }

    fun onServiceRequested() {
        Log.v(LOG_TAG, "onServiceRequested")
        workTimeServiceIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startService(workTimeServiceIntent)
    }


    fun onStatisticsActivityClicked() {
        updateWorkingTime()
    }

    fun onActivityDestroyed() {
        if (isTimerWorking()) {
            startUpdates()
        }
    }

    private fun updateWorkingTime() {
        if (MyTimer.measureDate != -1L) {
            dbHelper.updateWorkingTime(MyTimer.currentTimeSeconds, MyTimer.measureDate)
        }
    }

    private fun refreshTimerState() {
        if (isTimerWorking()) {
            view.onTimerButtonUpdate(R.string.stop)
        } else {
            view.onTimerButtonUpdate(R.string.start)
            view.onTimerUpdate(TimeFormatter.getTimeFromSeconds(dbHelper.getTodayWorkingTime()))
        }
    }

    private fun animateView(isRunning: Boolean) {
        if (isRunning) {
            view.onTimerStarted()
        } else {
            view.onTimerStopped()
        }
    }

    private fun isTimerWorking(): Boolean {
        return preferences.getLong(TIMER_START_TIME, -1L) != -1L
    }
}