package com.kakaboc.worktimer.statistic

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.util.concurrent.TimeUnit

/**
 * Created by Karol on 2017-10-21.
 */
class HourAxisValueFormatter : IAxisValueFormatter {

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return "${TimeUnit.SECONDS.toHours(value.toLong())}h"
    }

}