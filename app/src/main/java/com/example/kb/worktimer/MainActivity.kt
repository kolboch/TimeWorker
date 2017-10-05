package com.example.kb.worktimer

import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainView {

    private val LOG_TAG = "MainActivity"
    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = MainPresenter(this, applicationContext)
        presenter.initFakeData()
        onTimerButtonSetup()
        onChronometerSetup()
    }

    private fun onTimerButtonSetup() {
        timerButton.setOnClickListener {
            presenter.timerButtonClicked(chronometer.base)
        }
    }

    private fun onChronometerSetup() {
        Log.v(LOG_TAG, "OnChronometerSetup invoked")
        presenter.setupChronometer()
    }

    override fun onChronometerDisplayFormatChange(format: String) {
        chronometer.format = format
    }

    override fun onChronometerStopped() {
        chronometer.stop()
        timerButton.setText(R.string.start)
        animateButtonLeft(timerButton)
        Log.v(LOG_TAG, "chronometer stopAndUpdateWorkingTime call")
    }

    override fun onChronometerStarted() {
        chronometer.start()
        timerButton.setText(R.string.stop)
        animateButtonRight(timerButton)
        Log.v(LOG_TAG, "chronometer start call")
    }

    override fun onChronometerTimeUpdate(time: Long) {
        chronometer.base = time
    }

    private fun animateButtonRight(view: View) {
        val animation = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f)
        animation.interpolator = AccelerateDecelerateInterpolator()
        animation.duration = 700
        animation.start()
    }

    private fun animateButtonLeft(view: View) {
        val animation = ObjectAnimator.ofFloat(view, "rotation", 0f, -360f)
        animation.interpolator = AccelerateDecelerateInterpolator()
        animation.duration = 700
        animation.start()
    }

}
