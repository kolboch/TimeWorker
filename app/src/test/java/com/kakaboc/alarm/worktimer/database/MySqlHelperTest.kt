package com.kakaboc.alarm.worktimer.database

import android.content.Context
import android.os.Build
import com.kakaboc.alarm.worktimer.BuildConfig
import com.kakaboc.alarm.worktimer.model.WorkTime
import org.jetbrains.anko.db.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

/**
 * Created by Karlo on 2017-10-01.
 */
@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(Build.VERSION_CODES.KITKAT), manifest = "app/src/main/AndroidManifest.xml")
class MySqlHelperTest {

    private val todayDateFake = TimeUnit.DAYS.toMillis(200 * 365)
    private val timeInHoursToBeChanged = TimeUnit.HOURS.toMillis(7)
    private val timeInHoursChanged = TimeUnit.HOURS.toMillis(99)
    private var context: Context = RuntimeEnvironment.application
    private lateinit var dbHelper: MySqlHelper

    @Before
    fun setUp() {
        dbHelper = context.database
    }

    @After
    fun tearDown() {
        dbHelper.close()
    }

    @Test
    fun databaseCreated() {
        val readableDatabase = dbHelper.readableDatabase
        assertTrue("Database wasn't created", readableDatabase != null)
    }

    @Test
    fun databaseOpened() {
        val readableDatabase = dbHelper.readableDatabase
        assertTrue("Database is not opened", readableDatabase.isOpen)
    }

    @Test
    fun tableTimesCreatedAndIsEmpty() {
        val queryResult = dbHelper.use {
            select(TIMES_TABLE_NAME).exec { parseList(classParser<WorkTime>()) }
        }
        assertEquals(0, queryResult.size)
    }

    @Test
    fun insertEntriesIntoTableTimes() {
        insertData()
        assertEquals(3, queryTimesTable().size)
    }

    @Test
    fun deleteOneEntryAfterInsertingIntoTableTimes() {
        insertData()
        dbHelper.use {
            delete(TIMES_TABLE_NAME, "$TIMES_TABLE_DATE = $todayDateFake")
        }
        assertEquals(2, queryTimesTable().size)
    }

    @Test
    fun updateEntry() {
        insertData()
        dbHelper.use {
            update(TIMES_TABLE_NAME, TIMES_TABLE_TIME to timeInHoursChanged)
                    .whereSimple("$TIMES_TABLE_DATE = ?", "$todayDateFake").exec()
        }
        val result = dbHelper.use {
            select(TIMES_TABLE_NAME)
                    .whereSimple("$TIMES_TABLE_DATE = ?", "$todayDateFake")
                    .exec { parseList(classParser<WorkTime>()) }
        }
        assertEquals(1, result.size)
        assertEquals(timeInHoursChanged, result[0].timeWorked)
    }

    @Test(expected = android.database.sqlite.SQLiteException::class)
    fun noTableTimeWhenDroppedCalled() {
        dbHelper.use {
            dropTable(TIMES_TABLE_NAME)
        }
        dbHelper.use {
            select(TIMES_TABLE_NAME).exec { parseList(classParser<WorkTime>()) }
        }
    }

    private fun insertData() {
        dbHelper.use {
            delete(TIMES_TABLE_NAME)
            insert(TIMES_TABLE_NAME,
                    TIMES_TABLE_DATE to todayDateFake, TIMES_TABLE_TIME to timeInHoursToBeChanged)
            insert(TIMES_TABLE_NAME,
                    TIMES_TABLE_DATE to todayDateFake + 1, TIMES_TABLE_TIME to TimeUnit.HOURS.toMillis(2))
            insert(TIMES_TABLE_NAME,
                    TIMES_TABLE_DATE to todayDateFake + 2, TIMES_TABLE_TIME to TimeUnit.HOURS.toMillis(1))
        }
    }

    private fun queryTimesTable(): List<WorkTime> {
        return dbHelper.use {
            select(TIMES_TABLE_NAME).exec { parseList(classParser<WorkTime>()) }
        }
    }
}