package com.example.kb.worktimer

import android.content.Context
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
        monitor.startStop(timeBase,
                { isWorking: Boolean, timeBase: Long -> changeChronometerState(isWorking, timeBase) }
        )
    }

    private fun changeChronometerState(isWorking: Boolean, timeBase: Long) {
        if (isWorking) {
            view.onChronometerStopped()
        } else {
            view.onChronometerTimeUpdate(timeBase)
            view.onChronometerStarted()
        }
    }
}