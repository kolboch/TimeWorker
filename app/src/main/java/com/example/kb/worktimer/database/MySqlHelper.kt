package com.example.kb.worktimer.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.kb.worktimer.model.WorkTime
import com.example.kb.worktimer.model.getTodayDateInMillis
import org.jetbrains.anko.db.*
import java.util.*

/**
 * Created by Karlo on 2017-09-30.
 */

const val DB_NAME = "work_times_db"
const val TIMES_TABLE_NAME = "times"
const val TIMES_TABLE_DATE = "time_date"
const val TIMES_TABLE_TIME = "work_time"

class MySqlHelper private constructor(private val context: Context) : ManagedSQLiteOpenHelper(context, DB_NAME) {

    private val LOG_TAG = "MySqlHelper"

    companion object {
        private var instance: MySqlHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MySqlHelper {
            if (instance == null) {
                instance = MySqlHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.createTable(TIMES_TABLE_NAME,
                true,
                TIMES_TABLE_DATE to INTEGER + PRIMARY_KEY, // + NOT_NULL, waiting for fix from anko
                TIMES_TABLE_TIME to INTEGER + DEFAULT("0"))
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        // intentionally left empty
    }

    fun insertFakeData() {
        FakeDbData.insertEntries(context)
    }

    private fun logAllEntries() {
        //TODO delete before production
        val entries = context.database.use {
            select(TIMES_TABLE_NAME).exec { parseList(classParser<WorkTime>()) }
        }
        entries.forEach { Log.v(LOG_TAG, it.toString()) }
    }

    fun updateTodayWorkingTime(workingTime: Long) {
        val todayMillis = Calendar.getInstance().getTodayDateInMillis()

        context.database.use {
            replace(TIMES_TABLE_NAME,
                    TIMES_TABLE_DATE to todayMillis,
                    TIMES_TABLE_TIME to workingTime)
        }
    }

    fun getTodayWorkingTime(): Long {
        val todayMillis = Calendar.getInstance().getTodayDateInMillis()
        var result = getTodayWorkTime(todayMillis)

        if (result == null) {
            setupTodayWorkingTime(todayMillis)
            result = WorkTime(todayMillis, 0)
        }

        return result.timeWorked
    }

    private fun getTodayWorkTime(todayMillis: Long): WorkTime? {
        return context.database.use {
            select(TIMES_TABLE_NAME)
                    .whereSimple("$TIMES_TABLE_DATE = ?", "$todayMillis")
                    .exec { parseOpt(classParser()) }
        }
    }

    private fun setupTodayWorkingTime(todayMillis: Long) {
        context.database.use {
            insert(TIMES_TABLE_NAME,
                    TIMES_TABLE_DATE to todayMillis,
                    TIMES_TABLE_TIME to 0)
        }
    }

}