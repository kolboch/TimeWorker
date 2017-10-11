package com.example.kb.worktimer.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.kb.worktimer.database.MySqlHelper
import com.example.kb.worktimer.main.MainActivity
import com.example.kb.worktimer.model.TimeFormatter
import com.example.kb.worktimer.model.Timer

/**
 * Created by Karlo on 2017-10-06.
 */
const val ACTION_START = "com.example.kb.worktimer.action_start"
const val PENDING_INTENT_START = 111
const val ACTION_STOP = "com.example.kb.worktimer.action_stop"
const val PENDING_INTENT_STOP = 788
const val NOTIFICATION_ID = 14

class WorkTimeService : Service() {

    private val LOG_TAG = "WorkTimeService"
    private lateinit var databaseHelper: MySqlHelper
    private lateinit var notificationManager: NotificationManager

    private val startIntent by lazy {
        PendingIntent.getService(
                applicationContext,
                PENDING_INTENT_START,
                Intent(ACTION_START),
                0)
    }

    private val stopIntent by lazy {
        PendingIntent.getService(
                applicationContext,
                PENDING_INTENT_STOP,
                Intent(ACTION_STOP),
                0)
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        databaseHelper = MySqlHelper.getInstance(applicationContext)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        setUpTimerNotificationCallback()
        setUpNotification()
    }

    private fun setUpTimerNotificationCallback() {
        Timer.callbackNotification = { it ->
            val time = TimeFormatter.getTimeFromSeconds(it)
            updateNotification(time)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleIntentAction(intent?.action)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }

    private fun updateNotification(contentText: String) {
        notificationManager.notify(NOTIFICATION_ID, MyNotification.updateNotification(contentText))
    }

    private fun setUpNotification() {
        notificationManager.notify(
                NOTIFICATION_ID,
                MyNotification.createWorkTimeNotification(
                        applicationContext,
                        startIntent,
                        stopIntent
                )
        )
        Log.v(LOG_TAG, "Starting foreground")
    }

    private fun handleIntentAction(action: String?) {
        when (action) {
            ACTION_START -> Timer.startTimer()
            ACTION_STOP -> {
                Timer.stopTimer()
                startMainActivity()
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}