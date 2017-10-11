package com.example.kb.worktimer.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.example.kb.worktimer.R
import com.example.kb.worktimer.services.WorkTimeService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainView {

    //TODO how to init timer with proper time ? -> context from main to presenter?
    private val LOG_TAG = "MainActivity"
    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = MainPresenter(this)
        presenter.onActivityToTimerBind()
        startService(Intent(applicationContext, WorkTimeService::class.java))
        onTimerButtonSetup()
    }

    override fun onTimerUpdate(time: String) {
        timerDisplay.text = time
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
