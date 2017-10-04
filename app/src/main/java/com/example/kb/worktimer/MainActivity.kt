package com.example.kb.worktimer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
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
    }

    private fun onTimerButtonSetup() {
        timerButton.setOnClickListener {
            presenter.timerButtonClicked(chronometer.base)
        }
    }

    override fun onChronometerStopped() {
        chronometer.stop()
        timerButton.setText(R.string.start)
        Log.v(LOG_TAG, "chronometer stop call")
    }

    override fun onChronometerStarted() {
        chronometer.start()
        timerButton.setText(R.string.stop)
        Log.v(LOG_TAG, "chronometer start call")
    }

    override fun onChronometerTimeUpdate(time: Long) {
        chronometer.base = time
    }


}
