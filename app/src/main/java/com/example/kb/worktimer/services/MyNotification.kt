package com.example.kb.worktimer.services

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.example.kb.worktimer.R

/**
 * Created by Karlo on 2017-10-06.
 */
const val CHANNEL_ID = "com.example.kb.worktimer.default"

class MyNotification {
    companion object {
        private var builder: NotificationCompat.Builder? = null

        fun updateNotification(contentText: String): Notification? {
            builder?.setContentText(contentText)
            return builder?.build()
        }

        fun createWorkTimeNotification(context: Context,
                                       intentStart: PendingIntent,
                                       intentStop: PendingIntent): Notification {
            initBuilder(context)
            return builder!!.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.current_working_time))
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_tag_faces_black_24dp, context.getString(R.string.start), intentStart)
                    .addAction(R.drawable.ic_pause_black_24dp, context.getString(R.string.stop), intentStop)
                    .build()
        }

        private fun initBuilder(context: Context) {
            if (builder != null) {
                return
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = NotificationCompat.Builder(context, CHANNEL_ID)
            } else { // Android API below version O
                builder = NotificationCompat.Builder(context)
            }
        }
    }
}