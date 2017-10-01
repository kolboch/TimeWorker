package com.example.kb.worktimer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity(), MainView {

    private val LOG_TAG = "MainActivity"
    private var presenter: MainPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = MainPresenter(this, applicationContext)
        presenter?.onTimerStart()
    }


}
