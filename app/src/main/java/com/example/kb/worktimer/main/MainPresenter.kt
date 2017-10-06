package com.example.kb.worktimer.main

import android.util.Log
import com.example.kb.worktimer.services.WorkTimeService

/**
 * Created by Karlo on 2017-10-01.
 */
class MainPresenter(private val view: MainView) : ChronometerUpdater {

    private val LOG_TAG = "MainPresenter"
    private lateinit var workService: WorkTimeService


    fun attach(service: WorkTimeService) {
        Log.v(LOG_TAG, "attach called")
        workService = service
        workService.setChronometerUpdater(this)
    }

    fun detach() {
        workService.stopSelf()
    }

    fun onTimerButtonClicked(timeBase: Long) {
        workService.timerButtonClicked(timeBase)
    }

    fun onChronometerSetup() {
        workService.setupChronometer()
    }

    override fun onChronometerStopped() {
        view.onChronometerStopped()
    }

    override fun onChronometerStarted(timeBase: Long) {
        view.onChronometerTimeUpdate(timeBase)
        view.onChronometerStarted()
    }

    override fun updateChronometerTime(chronoBase: Long) {
        view.onChronometerTimeUpdate(chronoBase)
    }

    fun startWorkServiceForeground() {
        workService.setUpForeground()
    }
}