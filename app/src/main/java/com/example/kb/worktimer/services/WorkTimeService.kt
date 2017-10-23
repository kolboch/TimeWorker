package com.example.kb.worktimer.services

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.preference.PreferenceManager
import android.util.Log
import com.example.kb.worktimer.database.MySqlHelper
import com.example.kb.worktimer.main.MainActivity
import com.example.kb.worktimer.model.TimeFormatter
import com.example.kb.worktimer.model.Timer
import java.util.*


/**
 * Created by Karlo on 2017-10-06.
 */
const val TIMER_IS_WORKING = "com.example.kb.is_working_preferences"
const val ACTION_START = "com.example.kb.worktimer.action_start"
const val PENDING_INTENT_START = 111
const val ACTION_STOP = "com.example.kb.worktimer.action_stop"
const val PENDING_INTENT_STOP = 788
const val NOTIFICATION_ID = 14
const val WAKE_LOCK_TAG = "com.example.kb.worktimer.wake_lock"
const val PENDING_INTENT_SAVE_MIDNIGHT = 901
const val ACTION_SAVE = "com.example.kb.worktimer.action_save"

class WorkTimeService : Service() {

    private val LOG_TAG = "WorkTimeService"
    private val timer = Timer
    private var wakeLock: PowerManager.WakeLock? = null
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

    private val saveTimeIntent by lazy {
        PendingIntent.getService(baseContext, PENDING_INTENT_SAVE_MIDNIGHT, Intent(ACTION_SAVE), 0)
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
        acquireWakeLock()
        setUpTimerNotificationCallback()
        setUpTimerWakeLockCallbacks()
        setUpAlarmManagerTimerCallbacks()
        scheduleMidnightAction()
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleIntentAction(intent?.action)
        return START_STICKY
    }


    override fun onDestroy() {
        saveTimerState()
        stopForeground(true)
        cancelMidnightAction()
        if (wakeLock != null && wakeLock!!.isHeld) {
            wakeLock?.release()
        }
        super.onDestroy()
    }

    private fun refreshTimerState() {
        setUpWorkingTime()
        val wasWorking = preferences.getBoolean(TIMER_IS_WORKING, false)
        if (wasWorking) {
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
        timer.callbackNotification = { it ->
            val time = TimeFormatter.getTimeFromSeconds(it)
            updateNotification(time)
        }
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
    }

    private fun handleIntentAction(action: String?) {
        when (action) {
            ACTION_START -> {
                setUpWorkingTime()
                timer.startTimer()
                setUpAlarmManagerTimerCallbacks()
            }
            ACTION_STOP -> {
                timer.stopTimer()
                saveTimerState()
                startMainActivity()
                cancelMidnightAction()
                stopSelf()
            }
            ACTION_SAVE -> {
                Log.v(LOG_TAG, "Action save got called")
                saveTimerState()
                timer.setCurrentTimeAndUpdate(0)
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

    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG)
        wakeLock?.acquire()
    }

    private fun releaseWakeLock() {
        if (wakeLock != null && wakeLock!!.isHeld) {
            wakeLock?.release()
        }
    }

    private fun setUpTimerWakeLockCallbacks() {
        timer.acquireWakeLockCallback = { acquireWakeLock() }
        timer.releaseWakeLockCallback = { releaseWakeLock() }
    }

    private fun setUpAlarmManagerTimerCallbacks() {
        timer.scheduleAlarmManager = { scheduleMidnightAction() }
        timer.cancelAlarmManager = { cancelMidnightAction() }
    }

    private fun scheduleMidnightAction() {
        val midnightCalendar = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Calendar.getInstance(resources.configuration.locales[0])
        } else {
            Calendar.getInstance(resources.configuration.locale)
        }
        midnightCalendar.set(Calendar.HOUR_OF_DAY, 23)
        midnightCalendar.set(Calendar.MINUTE, 59)
        midnightCalendar.set(Calendar.SECOND, 40)
        midnightCalendar.set(Calendar.MILLISECOND, 0)
        val alarmManager = getSystemService(android.content.Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                midnightCalendar.timeInMillis,
                saveTimeIntent
        )
        Log.v(LOG_TAG, "Scheduling midnight action")
    }

    private fun cancelMidnightAction() {
        val alarmManager = getSystemService(android.content.Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(saveTimeIntent)
        Log.v(LOG_TAG, "Cancelling midnight action")
    }
}