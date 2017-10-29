package com.kakaboc.alarm.worktimer.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.util.Log
import com.kakaboc.alarm.worktimer.model.WorkTime
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

        private var userLocale: Locale? = null

        @Synchronized
        fun getInstance(ctx: Context): MySqlHelper {
            if (instance == null) {
                instance = MySqlHelper(ctx.applicationContext)
                userLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ctx.resources.configuration.locales[0]
                } else {
                    ctx.resources.configuration.locale
                }
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

    fun updateWorkingTime(workingTime: Long, measureDate: Long) {
        Log.v(LOG_TAG, "Updating today $measureDate")
        Log.v(LOG_TAG, "Updating working time $workingTime")
        context.database.use {
            replace(TIMES_TABLE_NAME,
                    TIMES_TABLE_DATE to measureDate,
                    TIMES_TABLE_TIME to workingTime)
        }
    }

    fun updateTodayWorkingTime(workingTime: Long) {
        val todayDaysMillis = getTodayTimeMillis()
        Log.v(LOG_TAG, "Updating today $todayDaysMillis")
        Log.v(LOG_TAG, "Updating working time $workingTime")
        context.database.use {
            replace(TIMES_TABLE_NAME,
                    TIMES_TABLE_DATE to todayDaysMillis,
                    TIMES_TABLE_TIME to workingTime)
        }
    }

    fun getTodayWorkingTime(): Long {
        val todayDaysMillis = getTodayTimeMillis()
        var result = getTodayWorkTime(todayDaysMillis)

        if (result == null) {
            setupTodayWorkingTime(todayDaysMillis)
            result = WorkTime(todayDaysMillis, 0)
        }

        return result.timeWorked
    }

    fun getWorkingStatistics(): List<WorkTime> {
        return context.database.use {
            select(TIMES_TABLE_NAME).exec { parseList(classParser()) }
        }
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

    fun getTodayTimeMillis(): Long {
        val calendar = Calendar.getInstance(userLocale)
        calendar.set(Calendar.HOUR_OF_DAY, 11)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun getCurrentTimeMillis(): Long {
        val calendar = Calendar.getInstance(userLocale)
        return calendar.timeInMillis
    }
}