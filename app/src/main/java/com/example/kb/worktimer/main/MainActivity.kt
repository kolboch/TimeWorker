package com.example.kb.worktimer.main

import android.animation.ObjectAnimator
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.example.kb.worktimer.R
import com.example.kb.worktimer.model.CHRONOMETER_FORMAT
import com.example.kb.worktimer.services.WorkTimeService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainView, ServiceConnection {

    private val LOG_TAG = "MainActivity"
    private lateinit var presenter: MainPresenter
    private var serviceBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = MainPresenter(this)
        onTimerButtonSetup()
        onChronometerSetup()
    }

    override fun onStart() {
        super.onStart()
        onServiceSetup()
    }

    override fun onStop() {
        super.onStop()
        if (serviceBound) {
            unbindService(this)
        }
    }

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        presenter.attach((binder as WorkTimeService.WorkTimeServiceBinder).getService())
        presenter.onChronometerSetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        presenter.detach()
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

    private fun onServiceSetup() {
        val intent = Intent(applicationContext, WorkTimeService::class.java)
        serviceBound = bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    private fun onTimerButtonSetup() {
        timerButton.setOnClickListener {
            presenter.onTimerButtonClicked(chronometer.base)
        }
    }

    private fun onChronometerSetup() {
        Log.v(LOG_TAG, "OnChronometerSetup invoked")
        chronometer.format = CHRONOMETER_FORMAT
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
