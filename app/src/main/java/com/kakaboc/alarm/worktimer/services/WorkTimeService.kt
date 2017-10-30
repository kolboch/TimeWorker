package com.kakaboc.alarm.worktimer.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import android.preference.PreferenceManager
import android.util.Log
import com.example.kb.worktimer.services.MyNotification
import com.kakaboc.alarm.worktimer.database.MySqlHelper
import com.kakaboc.alarm.worktimer.main.MainActivity
import com.kakaboc.alarm.worktimer.model.MyTimer
import com.kakaboc.alarm.worktimer.model.TimeFormatter
import java.util.concurrent.TimeUnit
import android.view.Display
import android.content.Context.DISPLAY_SERVICE
import android.hardware.display.DisplayManager
import android.os.Build


/**
 * Created by Karlo on 2017-10-06.
 */
const val TIMER_IS_WORKING = "com.kakaboc.alarm.worktimer.is_working_preferences"
const val ACTION_START = "com.kakaboc.alarm.worktimer.action_start"
const val PENDING_INTENT_START = 111
const val ACTION_STOP = "com.kakaboc.alarm.worktimer.action_stop"
const val PENDING_INTENT_STOP = 788
const val NOTIFICATION_ID = 14
const val SCREEN_OFF_TIME = "com.kakaboc.alarm.worktimer.screen_off_time_preferences"
const val ACTIVITY_DESTROYED_TIME = "com.kakaboc.alarm.worktimer.activity_destroyed_time"

class WorkTimeService : Service() {

    private val LOG_TAG = "WorkTimeService"
    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var preferences: SharedPreferences
    private lateinit var screenStateReceiver: ScreenStateReceiver

    private val startIntent by lazy {
        PendingIntent.getService(applicationContext, PENDING_INTENT_START, Intent(ACTION_START), 0)
    }

    private val stopIntent by lazy {
        PendingIntent.getService(applicationContext, PENDING_INTENT_STOP, Intent(ACTION_STOP), 0)
    }

    private val contentIntent by lazy {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        PendingIntent.getActivity(this, 0, intent, 0)
    }

    private lateinit var dbHelper: MySqlHelper
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        Log.v(LOG_TAG, "onCreate called")
        preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        dbHelper = MySqlHelper.getInstance(applicationContext)
        refreshTimerState()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        setUpNotification()
        setUpTimerNotificationCallback()
        setUpTimerSaveCallback()
        setUpScreenStateReceiver()
        onScreenStateUnknown()
    }

    private fun onScreenStateUnknown() {
        if (!isScreenOn()) {
            onScreenOff()
        }
    }

    private fun isScreenOn(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            dm.displays.any { it.state != Display.STATE_OFF }
        } else {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.isScreenOn
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleIntentAction(intent?.action)
        return START_STICKY
    }


    override fun onDestroy() {
        Log.v(LOG_TAG, "onDestroy called")
        saveTimerState(MyTimer.currentTimeSeconds, MyTimer.measureDate)
        stopForeground(true)
        if (wakeLock != null && wakeLock!!.isHeld) {
            wakeLock?.release()
        }
        unregisterReceiver(screenStateReceiver)
        super.onDestroy()
    }

    private fun refreshTimerState() {
        Log.v(LOG_TAG, "refreshTimerState called")
        setUpWorkingTime()
        val wasWorking = preferences.getBoolean(TIMER_IS_WORKING, false)
        if (wasWorking) {
            MyTimer.startTimer(dbHelper.getTodayTimeMillis())
            val destroyTime = preferences.getLong(ACTIVITY_DESTROYED_TIME, -1L)
            if (destroyTime != -1L) {
                val secondsPassed = TimeUnit.MILLISECONDS
                        .toSeconds(System.currentTimeMillis() - destroyTime)
                MyTimer.addPassedSeconds(secondsPassed)
                Log.v(LOG_TAG, "Destroy time retrieved $secondsPassed")
                preferences.edit().putLong(ACTIVITY_DESTROYED_TIME, -1L).apply()
            }
        }
    }

    private fun setUpWorkingTime() {
        Log.v(LOG_TAG, "setUpWorkingTimeCalled")
        val dbWorkingTime = dbHelper.getTodayWorkingTime()
        Log.v(LOG_TAG, "setUpWorkingTime timer seconds: ${MyTimer.currentTimeSeconds}")
        Log.v(LOG_TAG, "setUpWorkingTime db seconds: $dbWorkingTime")
        if (dbWorkingTime >= MyTimer.currentTimeSeconds) {
            Log.v(LOG_TAG, "setupWorkingTime -> setCurrentTimeAndUpdate($dbWorkingTime)")
            MyTimer.setCurrentTimeAndUpdate(dbWorkingTime, dbHelper.getTodayTimeMillis())
        }
    }

    private fun setUpTimerNotificationCallback() {
        MyTimer.callbackNotification = { it ->
            val time = TimeFormatter.getTimeFromSeconds(it)
            updateNotification(time)
        }
    }

    private fun saveTimerState(timeInSeconds: Long, measureDate: Long) {
        Log.v(LOG_TAG, "saveTimerState called")
        if (measureDate != -1L) {
            dbHelper.updateWorkingTime(timeInSeconds, measureDate)
        }
        preferences.edit().putBoolean(TIMER_IS_WORKING, MyTimer.isRunning).apply()
    }

    private fun updateNotification(contentText: String) {
        MyNotification.updateNotification(contentText)
        notificationManager.notify(NOTIFICATION_ID, MyNotification.rebuild())
    }

    private fun setUpNotification() {
        notificationManager.notify(
                NOTIFICATION_ID,
                MyNotification.createWorkTimeNotification(
                        baseContext,
                        startIntent,
                        stopIntent,
                        contentIntent
                )
        )
    }

    private fun handleIntentAction(action: String?) {
        when (action) {
            ACTION_START -> {
                onTimerStartedActions()
                onScreenStateUnknown()
            }
            ACTION_STOP -> {
                onTimerStoppedActions()
            }
            else -> {
                refreshTimerState()
                onScreenStateUnknown()
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun onScreenOff() {
        Log.v(LOG_TAG, "Registered screen off!!")
        if (MyTimer.isRunning) {
            MyTimer.stopTimer()
            preferences.edit()
                    .putBoolean(TIMER_IS_WORKING, true)
                    .putLong(SCREEN_OFF_TIME, dbHelper.getCurrentTimeMillis())
                    .apply()
        }
    }

    private fun onScreenOn() {
        Log.v(LOG_TAG, "Registered screen on!!")
        val wasRunning = preferences.getBoolean(TIMER_IS_WORKING, false)
        Log.v(LOG_TAG, "wasRunning $wasRunning")
        if (wasRunning) {
            val offTimeMillis = preferences.getLong(SCREEN_OFF_TIME, dbHelper.getCurrentTimeMillis())
            val timePassed = dbHelper.getCurrentTimeMillis() - offTimeMillis
            MyTimer.resumeTimer(TimeUnit.MILLISECONDS.toSeconds(timePassed))
        }
    }

    private fun setUpScreenStateReceiver() {
        screenStateReceiver = ScreenStateReceiver({ onScreenOn() }, { onScreenOff() })
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenStateReceiver, filter)
    }

    private fun setUpTimerSaveCallback() {
        MyTimer.saveTimerState = { time, date -> saveTimerState(time, date) }
    }

    private fun onTimerStartedActions() {
        setUpWorkingTime()
        MyTimer.startTimer(dbHelper.getTodayTimeMillis())
    }

    private fun onTimerStoppedActions() {
        MyTimer.stopTimer()
        startMainActivity()
        stopSelf()
    }
}