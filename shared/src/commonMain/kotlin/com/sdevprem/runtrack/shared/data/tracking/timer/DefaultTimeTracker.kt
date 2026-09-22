package com.sdevprem.runtrack.shared.data.tracking.timer

import com.sdevprem.runtrack.shared.domain.tracking.timer.TimeTracker
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class DefaultTimeTracker(
    private val applicationScope: CoroutineScope,
    private val defaultDispatcher: CoroutineDispatcher
) : TimeTracker {
    private var timeElapsedInMillis = 0L
    private var isRunning = false
    private var callback: ((timeInMillis: Long) -> Unit)? = null
    private var job: Job? = null

    private fun start() {
        if (job != null)
            return

        job = applicationScope.launch(defaultDispatcher) {
            while (isRunning && isActive) {
                callback?.invoke(timeElapsedInMillis)
                delay(1000)
                timeElapsedInMillis += 1000
            }
        }
    }

    override fun startResumeTimer(callback: (timeInMillis: Long) -> Unit) {
        if (isRunning)
            return
        this.callback = callback
        isRunning = true
        start()
    }

    override fun stopTimer() {
        pauseTimer()
        timeElapsedInMillis = 0
    }

    override fun pauseTimer() {
        isRunning = false
        job?.cancel()
        job = null
        callback = null
    }

}