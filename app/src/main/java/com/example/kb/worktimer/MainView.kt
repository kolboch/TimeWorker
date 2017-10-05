package com.example.kb.worktimer

/**
 * Created by Karlo on 2017-10-01.
 */
interface MainView {

    fun onChronometerTimeUpdate(time: Long)

    fun onChronometerStopped()
    fun onChronometerStarted()
    fun onChronometerDisplayFormatChange(format: String)
}