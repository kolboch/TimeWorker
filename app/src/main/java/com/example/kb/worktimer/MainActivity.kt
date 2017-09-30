package com.example.kb.worktimer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.kb.worktimer.database.*
import com.example.kb.worktimer.model.WorkTime
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.select

class MainActivity : AppCompatActivity() {

    private val LOG_TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FakeDbData.insertEntries(this)
        val entries = database.use {
            select(TIMES_TABLE_NAME).exec { parseList(classParser<WorkTime>()) }
        }
        entries.forEach { Log.v(LOG_TAG, it.toString()) }
    }
}
