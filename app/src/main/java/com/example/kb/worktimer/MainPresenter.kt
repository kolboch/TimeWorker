package com.example.kb.worktimer

import android.content.Context
import android.util.Log
import com.example.kb.worktimer.database.MySqlHelper
import com.example.kb.worktimer.model.ChronometerMonitor

/**
 * Created by Karlo on 2017-10-01.
 */
class MainPresenter(
        private val view: MainView,
        context: Context
) {

    private val LOG_TAG = "MainPresenter"
    private val monitor = ChronometerMonitor()
    private val databaseHelper = MySqlHelper.getInstance(context)

    fun initFakeData() {
        databaseHelper.insertFakeData()
    }

    fun timerButtonClicked(timeBase: Long) {
        monitor.startStop(timeBase, { wasWorking: Boolean, timeBase: Long, workingTime: Long ->
            changeChronometerState(wasWorking, timeBase, workingTime)
        })
    }

    private fun changeChronometerState(wasWorking: Boolean, timeBase: Long, workingTime: Long) {
        if (wasWorking) {
            view.onChronometerStopped()
            databaseHelper.updateTodayWorkingTime(workingTime)
        } else {
            view.onChronometerTimeUpdate(timeBase)
            view.onChronometerStarted()
        }
    }

    fun setupChronometer() {
        val savedWorkingTime = databaseHelper.getTodayWorkingTime()
        Log.v(LOG_TAG, "Saved working time: $savedWorkingTime")
        val chronoBase = monitor.getChronoTimeBaseAndSetup(savedWorkingTime)
        view.onChronometerTimeUpdate(chronoBase)
    }
}