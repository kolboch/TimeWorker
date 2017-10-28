package com.kakaboc.alarm.worktimer.statistic

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Karol on 2017-10-17.
 */
class DateAxisValueFormatter : IAxisValueFormatter {
    private var sdf = SimpleDateFormat("dd-MM")

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        // as value is in days for proper date we need to convert back to timestamp
        // the reason is mpAndroidChart Lib display issue when x's values where in millis
        return sdf.format(Date(TimeUnit.DAYS.toMillis(value.toLong())))
    }

}