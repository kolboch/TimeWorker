package com.kakaboc.worktimer.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.PRIORITY_MAX
import com.kakaboc.worktimer.R

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

        @SuppressLint("NewApi")
        fun createWorkTimeNotification(context: Context,
                                       intentStart: PendingIntent,
                                       intentStop: PendingIntent,
                                       contentIntent: PendingIntent): Notification {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelName = context.getString(R.string.channel_name)
                val channelDescription = context.getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_LOW
                val channel = NotificationChannel(CHANNEL_ID, channelName, importance)
                channel.description = channelDescription
                channel.setShowBadge(false)
                val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
            if (builder != null) {
                return builder!!.build()
            }
            initBuilder(context)
            return builder!!.setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.current_working_time))
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_tag_faces_white_24dp, context.getString(R.string.start), intentStart)
                    .addAction(R.drawable.ic_pause_white_24dp, context.getString(R.string.stop), intentStop)
                    .setContentIntent(contentIntent)
                    .setPriority(PRIORITY_MAX)
                    .build()
        }

        private fun initBuilder(context: Context) {
            builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationCompat.Builder(context, CHANNEL_ID)
            } else { // Android API below version O
                NotificationCompat.Builder(context)
            }
        }

        fun rebuild(): Notification? {
            return builder?.build()
        }
    }
}