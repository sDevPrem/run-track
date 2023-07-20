package com.sdevprem.runtrack.core.tracking

import com.sdevprem.runtrack.di.ApplicationScope
import com.sdevprem.runtrack.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimeTracker @Inject constructor(
    @ApplicationScope private val applicationScope: CoroutineScope,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {
    private var timeElapsedInMillis = 0L
    private var isRunning = false
    private var callback: ((timeInMillis: Long) -> Unit)? = null
    private var job: Job? = null

    private fun start() {
        if (job != null)
            return
        System.currentTimeMillis()
        this.job = applicationScope.launch(defaultDispatcher) {
            while (isRunning && isActive) {
                callback?.invoke(timeElapsedInMillis)
                delay(1000)
                timeElapsedInMillis += 1000
            }
        }
    }

    fun startResumeTimer(callback: (timeInMillis: Long) -> Unit) {
        if (isRunning)
            return
        this.callback = callback
        isRunning = true
        start()
    }

    fun stopTimer() {
        pauseTimer()
        timeElapsedInMillis = 0
    }

    fun pauseTimer() {
        isRunning = false
        job?.cancel()
        job = null
        callback = null
    }

}