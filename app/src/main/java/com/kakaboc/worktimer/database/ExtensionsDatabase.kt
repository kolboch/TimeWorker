package com.kakaboc.worktimer.database

import android.content.Context

/**
 * Created by Karlo on 2017-09-30.
 */

val Context.database: MySqlHelper
    get() = MySqlHelper.getInstance(applicationContext)
