package com.example.kb.worktimer.statistic

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import com.example.kb.worktimer.R
import com.example.kb.worktimer.database.MySqlHelper
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity_statistics.*
import java.util.concurrent.TimeUnit

class StatisticsActivity : AppCompatActivity() {

    private val LOG_TAG = "StatisticsActivity"
    //TODO refactor to MVP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        setWindowDisplay()
        val dbHelper = MySqlHelper.getInstance(applicationContext)
        dbHelper.insertFakeData()

        var data = dbHelper.getWorkingStatistics()
        val entries = mutableListOf<BarEntry>()
        data.forEach {
            Log.v(LOG_TAG, "timeWorked: ${it.timeWorked}")
            val dateInDays = TimeUnit.MILLISECONDS.toDays(it.date)
            entries.add(BarEntry(dateInDays.toFloat(), it.timeWorked.toFloat()))
        }
        val dataSet = BarDataSet(entries, "Test Label")
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        val lineData = BarData(dataSet)

//        styling
        chart.setDrawBarShadow(false)
        chart.setDrawValueAboveBar(true)
        chart.setPinchZoom(false)
        chart.description.isEnabled = false
//        end of styling

        val xAxis = chart.xAxis
        xAxis.valueFormatter = DateAxisValueFormatter()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)

        chart.data = lineData
        chart.invalidate()
    }

    private fun setWindowDisplay() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}
