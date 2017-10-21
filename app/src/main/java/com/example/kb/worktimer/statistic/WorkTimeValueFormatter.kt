package com.example.kb.worktimer.statistic

import com.example.kb.worktimer.model.TimeFormatter
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Created by Karol on 2017-10-21.
 */
class WorkTimeValueFormatter : IValueFormatter {

    override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?): String {
        return TimeFormatter.getTimeFromSeconds(value.toLong())
    }

}