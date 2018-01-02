package com.kakaboc.worktimer.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.preference.PreferenceManager
import android.util.Log
import android.view.Display

import com.kakaboc.worktimer.database.MySqlHelper
import com.kakaboc.worktimer.main.MainActivity
import com.kakaboc.worktimer.model.MyTimer
import com.kakaboc.worktimer.model.MyTimer.computeStartingTime
import com.kakaboc.worktimer.model.TimeFormatter


/**
 * Created by Karlo on 2017-10-06.
 */
const val TIMER_START_TIME = "com.kakaboc.worktimer.start_time"
const val ACTION_START = "com.kakaboc.worktimer.action_start"
const val PENDING_INTENT_START = 111
const val ACTION_STOP = "com.kakaboc.worktimer.action_stop"
const val PENDING_INTENT_STOP = 788
const val NOTIFICATION_ID = 14

class WorkTimeService : Service() {

    private val LOG_TAG = "WorkTimeService"
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
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        setUpNotification()
        setUpTimerNotificationCallback()
        setUpTimerCallbacks()
        setUpScreenStateReceiver()
        onScreenStateUnknown()
    }

    private fun onScreenStateUnknown() {
        if (isScreenOn()) {
            onScreenOn()
        } else {
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

    override fun onLowMemory() {
        Log.v(LOG_TAG, "onLowMemory CALL")
        super.onLowMemory()
    }

    override fun onDestroy() {
        Log.v(LOG_TAG, "onDestroy called")
        saveTimerState(MyTimer.currentTimeSeconds, MyTimer.measureDate)
        stopForeground(true)
        unregisterReceiver(screenStateReceiver)
        super.onDestroy()
    }

    private fun setUpTimerNotificationCallback() {
        MyTimer.updateNotificationUI = { it ->
            val time = TimeFormatter.getTimeFromSeconds(it)
            updateNotification(time)
        }
    }

    private fun saveTimerState(timeInSeconds: Long, measureDate: Long) {
        Log.v(LOG_TAG, "saveTimerState called, time: $timeInSeconds, $measureDate")
        if (measureDate != -1L) {
            dbHelper.updateWorkingTime(timeInSeconds, measureDate)
        }
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
        Log.v(LOG_TAG, "onScreenOff")
        MyTimer.stopUpdates()
    }

    private fun onScreenOn() {
        Log.v(LOG_TAG, "onScreenOn")
        val startTime = preferences.getLong(TIMER_START_TIME, -1L)
        if (startTime != -1L) {
            MyTimer.setUpTimer(startTime, dbHelper.getDayTimeInMillis())
            MyTimer.startUpdates()
        }
    }

    private fun setUpScreenStateReceiver() {
        screenStateReceiver = ScreenStateReceiver({ onScreenOn() }, { onScreenOff() })
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenStateReceiver, filter)
    }

    private fun setUpTimerCallbacks() {
        MyTimer.saveTimerState = { time, date -> saveTimerState(time, date) }
        MyTimer.clearStartTime = { clearSavedStartTime() }
        MyTimer.setStartTime = { time -> saveStartTime(time) }
        MyTimer.getCurrentTimeMillis = { dbHelper.getCurrentTimeMillis() }
    }

    private fun saveStartTime(time: Long) {
        Log.v(LOG_TAG, "saveing start time to shared prefs $time")
        preferences.edit()
                .putLong(TIMER_START_TIME, time)
                .apply()
    }

    private fun clearSavedStartTime() {
        Log.v(LOG_TAG, "clearing start time in shared prefs")
        preferences.edit()
                .putLong(TIMER_START_TIME, -1L)
                .apply()
    }

    private fun onTimerStartedActions() {
        MyTimer.startTimer(computeStartingTime(dbHelper.getCurrentTimeMillis(), dbHelper.getTodayWorkingTime()), dbHelper.getDayTimeInMillis())
    }

    private fun onTimerStoppedActions() {
        MyTimer.stopTimer()
        startMainActivity()
        stopForeground(true)
        stopSelf()
    }
}