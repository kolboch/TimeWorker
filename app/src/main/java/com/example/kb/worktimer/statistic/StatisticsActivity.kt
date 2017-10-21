package com.example.kb.worktimer.statistic

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import com.example.kb.worktimer.R
import com.example.kb.worktimer.database.MySqlHelper
import com.example.kb.worktimer.model.WorkTime
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.activity_statistics.*
import java.util.concurrent.TimeUnit

class StatisticsActivity : AppCompatActivity() {

    private val LOG_TAG = "StatisticsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        setWindowDisplay()
        var workingRows = getWorkingStatistics()
        val entries = convertRowsToEntries(workingRows)
        val dataSet = BarDataSet(entries, getString(R.string.working_data_label))
        styleDataSet(dataSet)
        val barData = BarData(dataSet)
        styleChart()
        styleAxes()
        chart.data = barData
        chart.invalidate()
    }

    private fun styleAxes() {
        styleXAxis()
        styleYAxis(chart.axisLeft)
        styleYAxis(chart.axisRight)
    }

    private fun styleDataSet(dataSet: BarDataSet) {
        dataSet.colors = listOf(
                ContextCompat.getColor(applicationContext, R.color.chart_bar_color)
        )
        dataSet.valueTextColor = ContextCompat.getColor(applicationContext, R.color.chart_text)
        dataSet.valueFormatter = WorkTimeValueFormatter()
    }

    private fun styleXAxis() {
        val xAxis = chart.xAxis
        xAxis.valueFormatter = DateAxisValueFormatter()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1F
        xAxis.textColor = ContextCompat.getColor(applicationContext, R.color.chart_text)
    }

    private fun styleYAxis(yAxis: YAxis) {
        yAxis.textColor = ContextCompat.getColor(applicationContext, R.color.chart_text)
        yAxis.labelCount = 25
        yAxis.axisMaximum = TimeUnit.HOURS.toSeconds(24).toFloat()
        yAxis.axisMinimum = 0f
        yAxis.granularity = TimeUnit.HOURS.toSeconds(1).toFloat()
        yAxis.valueFormatter = HourAxisValueFormatter()
    }

    private fun convertRowsToEntries(workingRows: List<WorkTime>): MutableList<BarEntry>? {
        val entries = mutableListOf<BarEntry>()
        workingRows.forEach {
            Log.v(LOG_TAG, "timeWorked: ${it.timeWorked}")
            val dateInDays = TimeUnit.MILLISECONDS.toDays(it.date)
            entries.add(BarEntry(dateInDays.toFloat(), it.timeWorked.toFloat()))
        }
        return entries
    }

    private fun getWorkingStatistics(): List<WorkTime> {
        val dbHelper = MySqlHelper.getInstance(applicationContext)
        return dbHelper.getWorkingStatistics()
    }

    private fun setWindowDisplay() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    private fun styleChart() {
        chart.setDrawBarShadow(false)
        chart.setDrawValueAboveBar(true)
        chart.setPinchZoom(false)
        chart.description.isEnabled = false
        chart.legend.textColor = ContextCompat.getColor(applicationContext, R.color.chart_text)
    }
}
