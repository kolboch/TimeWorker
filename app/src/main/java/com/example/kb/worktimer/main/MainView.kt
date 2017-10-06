package com.example.kb.worktimer.main

/**
 * Created by Karlo on 2017-10-01.
 */
interface MainView {

    fun onChronometerTimeUpdate(time: Long)

    fun onChronometerStopped()
    fun onChronometerStarted()
}