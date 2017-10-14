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

        fun updateNotification(contentText: String) {
            builder?.setContentText(contentText)
        }

        fun createWorkTimeNotification(context: Context,
                                       intentStart: PendingIntent,
                                       intentStop: PendingIntent,
                                       contentIntent: PendingIntent): Notification {

            if (builder != null) {
                return builder!!.build()
            }
            initBuilder(context)
            return builder!!.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.current_working_time))
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_tag_faces_white_24dp, context.getString(R.string.start), intentStart)
                    .addAction(R.drawable.ic_pause_white_24dp, context.getString(R.string.stop), intentStop)
                    .setContentIntent(contentIntent)
                    .build()
        }

        private fun initBuilder(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = NotificationCompat.Builder(context, CHANNEL_ID)
            } else { // Android API below version O
                builder = NotificationCompat.Builder(context)
            }
        }

        fun rebuild(): Notification? {
            return builder?.build()
        }
    }
}