package com.example.kb.worktimer.services

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.support.v4.app.NotificationCompat
import com.example.kb.worktimer.R

/**
 * Created by Karlo on 2017-10-06.
 */
const val CHANNEL_ID = "com.example.kb.worktimer.default"

class MyNotification {
    //TODO custom notification, where chronometer will have listener, there i would pass callback for it, or still those nasty buttons ;)
    companion object {
        fun getWorkTimerNotification(context: Context,
                                     chronometerBase: Long,
                                     actionStart: PendingIntent,
                                     actionStop: PendingIntent): Notification {
            return NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.current_working_time))
                    .setUsesChronometer(true)
                    .setWhen(chronometerBase)
                    .setAutoCancel(false)
                    .addAction(R.drawable.ic_tag_faces_black_24dp, context.getString(R.string.start), actionStart)
                    .addAction(R.drawable.ic_pause_black_24dp, context.getString(R.string.stop), actionStop)
                    .build()
        }
    }
}