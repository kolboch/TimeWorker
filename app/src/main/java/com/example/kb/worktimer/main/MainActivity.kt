package com.example.kb.worktimer.main

import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.example.kb.worktimer.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainView {

    private val LOG_TAG = "MainActivity"
    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = MainPresenter(this, applicationContext)
        presenter.onActivityToTimerBind()
        onTimerButtonSetup()
    }

    override fun onDestroy() {
        presenter.onActivityDestroyed()
        presenter.onServiceRequested()
        super.onDestroy()
    }

    override fun onTimerUpdate(time: String) {
        runOnUiThread {
            timerDisplay.text = time
        }
    }

    override fun onTimerStopped() {
        timerButton.text = getString(R.string.start)
        animateButtonLeft(timerButton)
    }

    override fun onTimerStarted() {
        presenter.onServiceRequested()
        timerButton.text = getString(R.string.stop)
        animateButtonRight(timerButton)
    }

    override fun onTimerButtonUpdate(stringResource: Int) {
        timerButton.text = getString(stringResource)
    }

    private fun onTimerButtonSetup() {
        timerButton.setOnClickListener {
            presenter.startStopTimer()
        }
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
