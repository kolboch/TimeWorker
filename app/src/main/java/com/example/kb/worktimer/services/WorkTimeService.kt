package com.example.kb.worktimer.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import com.example.kb.worktimer.database.MySqlHelper
import com.example.kb.worktimer.main.MainActivity
import com.example.kb.worktimer.model.TimeFormatter
import com.example.kb.worktimer.model.Timer

/**
 * Created by Karlo on 2017-10-06.
 */
const val TIMER_IS_WORKING = "com.example.kb.is_working_preferences"
const val ACTION_START = "com.example.kb.worktimer.action_start"
const val PENDING_INTENT_START = 111
const val ACTION_STOP = "com.example.kb.worktimer.action_stop"
const val PENDING_INTENT_STOP = 788
const val NOTIFICATION_ID = 14

class WorkTimeService : Service() {

    private val LOG_TAG = "WorkTimeService"
    private val timer = Timer
    private lateinit var preferences: SharedPreferences

    private val startIntent by lazy {
        PendingIntent.getService(baseContext, PENDING_INTENT_START, Intent(ACTION_START), 0)
    }

    private val stopIntent by lazy {
        PendingIntent.getService(baseContext, PENDING_INTENT_STOP, Intent(ACTION_STOP), 0)
    }

    private val contentIntent by lazy {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        PendingIntent.getActivity(this, 0, intent, 0)
    }

    private lateinit var databaseHelper: MySqlHelper
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        databaseHelper = MySqlHelper.getInstance(applicationContext)
        refreshTimerState()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        setUpNotification()
        setUpTimerNotificationCallback()
    }

    override fun onBind(p0: Intent?): IBinder? = null

    private fun refreshTimerState() {
        setUpWorkingTime()
        val wasWorking = preferences.getBoolean(TIMER_IS_WORKING, false)
        Log.v(LOG_TAG, "wasWorking shared preferences: --> $wasWorking")
        if (wasWorking) {
            Log.v(LOG_TAG, "starting timer inside Refresh Timer State")
            timer.startTimer()
        }
    }

    private fun setUpWorkingTime() {
        val workingTime = databaseHelper.getTodayWorkingTime()
        if (workingTime > timer.currentTimeSeconds) {
            timer.setCurrentTimeAndUpdate(workingTime)
        }
    }

    private fun setUpTimerNotificationCallback() {
        Log.v(LOG_TAG, "Setting notification callback")
        timer.callbackNotification = { it ->
            val time = TimeFormatter.getTimeFromSeconds(it)
            updateNotification(time)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.v(LOG_TAG, "Can i catch that log from onStartCommand;) ?")
        handleIntentAction(intent?.action)
        return START_STICKY
    }


    override fun onDestroy() {
        saveTimerState()
        stopForeground(true)
        super.onDestroy()
    }

    private fun saveTimerState() {
        databaseHelper.updateTodayWorkingTime(timer.currentTimeSeconds)
        preferences.edit().putBoolean(TIMER_IS_WORKING, timer.isRunning).apply()
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
        Log.v(LOG_TAG, "Starting notification")
    }

    private fun handleIntentAction(action: String?) {
        when (action) {
            ACTION_START -> {
                setUpWorkingTime()
                timer.startTimer()
            }
            ACTION_STOP -> {
                timer.stopTimer()
                saveTimerState()
                startMainActivity()
                stopSelf()
            }
            else -> {
                refreshTimerState()
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}