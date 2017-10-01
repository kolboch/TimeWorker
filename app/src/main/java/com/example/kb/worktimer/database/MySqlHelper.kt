package com.example.kb.worktimer.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.kb.worktimer.model.WorkTime
import org.jetbrains.anko.db.*

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
        val entries = context.database.use {
            select(TIMES_TABLE_NAME).exec { parseList(classParser<WorkTime>()) }
        }
        entries.forEach { Log.v(LOG_TAG, it.toString()) }
    }

}