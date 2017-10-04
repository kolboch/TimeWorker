package com.example.kb.worktimer.model

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Spy

/**
 * Created by Karol on 2017-10-04.
 */
class ChronometerMonitorTest {

    private val callback = { _: Boolean, _: Long -> Unit }

    @Spy private
    lateinit var monitor: ChronometerMonitor

    @Before
    fun setUp() {
        monitor = Mockito.spy(ChronometerMonitor(false, 0))
    }

    @Test
    fun whenSetupThenWorkingTimeChanged() {
        monitor.getChronoTimeBaseAndSetup(100)

        assertEquals(100, monitor.currentWorkTime)
    }

    @Test
    fun whenWorkingStopCalled() {
        monitor.isWorking = true

        monitor.startStop(1, callback)
        
        verify(monitor).stop(1)
        verify(monitor, never()).start()
    }

    @Test
    fun whenNotWorkingStartCalled() {
        monitor.isWorking = false

        monitor.startStop(1, callback)

        verify(monitor).start()
        verify(monitor, never()).stop(any())
    }

    @Test
    fun whenStartedThenWorking() {
        monitor.start()

        assertEquals(true, monitor.isWorking)
    }

    @Test
    fun whenStoppedThenWorkingFalse() {
        monitor.stop(any())

        assertEquals(false, monitor.isWorking)
    }

    @Test
    fun whenStoppedWorkingTimeUpdated() {
        monitor.getChronoTimeBaseAndSetup(1790)

        assertEquals(1790, monitor.currentWorkTime)
    }
}