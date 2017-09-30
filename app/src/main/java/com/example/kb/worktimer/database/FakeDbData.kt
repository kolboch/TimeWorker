package com.example.kb.worktimer.database

import android.content.Context
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insert
import java.util.*

/**
 * Created by Karlo on 2017-09-30.
 */
class FakeDbData {
    companion object {
        private const val DAY = 24 * 60 * 60 * 1000
        private val today = Date()

        fun insertEntries(context: Context) {
            context.database.use {
                delete(TIMES_TABLE_NAME)
                insert(TIMES_TABLE_NAME,
                        TIMES_TABLE_DATE to today.time - DAY, TIMES_TABLE_TIME to 3 * 60 * 60 * 1000)
                insert(TIMES_TABLE_NAME,
                        TIMES_TABLE_DATE to today.time - 2 * DAY, TIMES_TABLE_TIME to 2 * 60 * 60 * 1000)
                insert(TIMES_TABLE_NAME,
                        TIMES_TABLE_DATE to today.time - 3 * DAY, TIMES_TABLE_TIME to 1 * 60 * 60 * 1000)
                insert(TIMES_TABLE_NAME,
                        TIMES_TABLE_DATE to today.time - 4 * DAY, TIMES_TABLE_TIME to 4 * 60 * 60 * 1000)
                insert(TIMES_TABLE_NAME,
                        TIMES_TABLE_DATE to today.time - 5 * DAY, TIMES_TABLE_TIME to 5 * 60 * 60 * 1000)
                insert(TIMES_TABLE_NAME,
                        TIMES_TABLE_DATE to today.time - 6 * DAY, TIMES_TABLE_TIME to 7 * 60 * 60 * 1000)
            }
        }
    }
}