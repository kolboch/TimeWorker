package com.example.kb.worktimer.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.kb.worktimer.database.MySqlHelper
import com.example.kb.worktimer.main.ChronometerUpdater
import com.example.kb.worktimer.model.ChronometerMonitor

/**
 * Created by Karlo on 2017-10-06.
 */
const val ACTION_START = "com.example.kb.worktimer.action_start"
const val ACTION_STOP = "com.example.kb.worktimer.action_stop"

class WorkTimeService : Service() {

    private val LOG_TAG = "WorkTimeService"
    private lateinit var updater: ChronometerUpdater
    private val binder = WorkTimeServiceBinder()
    private val monitor = ChronometerMonitor()
    private lateinit var databaseHelper: MySqlHelper

    inner class WorkTimeServiceBinder : Binder() {
        fun getService(): WorkTimeService {
            return this@WorkTimeService
        }
    }

    override fun onBind(p0: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        Log.v(LOG_TAG, "onCreate called")
        databaseHelper = MySqlHelper.getInstance(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.v(LOG_TAG, "onStartCommand called")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun initFakeData() {
        databaseHelper.insertFakeData()
    }

    fun setChronometerUpdater(updater: ChronometerUpdater) {
        this.updater = updater
    }

    private fun changeChronometerState(wasWorking: Boolean, timeBase: Long, workingTime: Long) {
        if (wasWorking) {
            updater.onChronometerStopped()
            databaseHelper.updateTodayWorkingTime(workingTime)
        } else {
            updater.onChronometerStarted(timeBase)
        }
    }

    fun setupChronometer() {
        val savedWorkingTime = databaseHelper.getTodayWorkingTime()
        Log.v(LOG_TAG, "Saved working time: $savedWorkingTime")
        val chronometerBase = monitor.getChronoTimeBaseAndSetup(savedWorkingTime)
        updater.updateChronometerTime(chronometerBase)
    }

    fun timerButtonClicked(timeBase: Long) {
        monitor.startStop(timeBase, { wasWorking: Boolean, timeBase: Long, workingTime: Long ->
            changeChronometerState(wasWorking, timeBase, workingTime)
        })
    }
}