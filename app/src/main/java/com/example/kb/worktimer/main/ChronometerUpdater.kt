package com.example.kb.worktimer.main

/**
 * Created by Karlo on 2017-10-06.
 */
interface ChronometerUpdater {

    fun onChronometerStopped()
    fun onChronometerStarted(timebase: Long)
    fun updateChronometerTime(chronoBase: Long)

}