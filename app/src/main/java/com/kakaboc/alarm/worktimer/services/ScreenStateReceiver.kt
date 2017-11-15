package com.kakaboc.alarm.worktimer.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by Karlo on 2017-10-29.
 */
class ScreenStateReceiver(
        private val screenOnCallback: () -> Unit,
        private val screenOffCallback: () -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SCREEN_OFF -> screenOffCallback.invoke()
            Intent.ACTION_SCREEN_ON -> screenOnCallback.invoke()
        }
    }
}