package com.kakaboc.alarm.worktimer.main

/**
 * Created by Karlo on 2017-10-01.
 */
interface MainView {

    fun onTimerUpdate(time: String)
    fun onTimerStopped()
    fun onTimerStarted()
    fun onTimerButtonUpdate(stringResource: Int)
}