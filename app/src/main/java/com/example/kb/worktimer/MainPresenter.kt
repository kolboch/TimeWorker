package com.example.kb.worktimer

import android.content.Context
import android.util.Log
import com.example.kb.worktimer.database.FakeDbData
import com.example.kb.worktimer.database.MySqlHelper
import com.example.kb.worktimer.database.TIMES_TABLE_NAME
import com.example.kb.worktimer.database.database
import com.example.kb.worktimer.model.WorkTime
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.select

/**
 * Created by Karlo on 2017-10-01.
 */
class MainPresenter(val view: MainView, context: Context) {

    private val LOG_TAG = "MainPresenter"
    private var timer: MyTimer = MyTimer()
    private val databaseHelper = MySqlHelper.getInstance(context)

    fun initFakeData() {
        databaseHelper.insertFakeData()
    }

    fun onTimerStart() {
        timer.startTimer()
    }

    fun onTimerStop() {

    }
}