package com.sdevprem.runtrack.shared.domain.tracking.timer

interface TimeTracker {
    fun startResumeTimer(callback: (timeInMillis: Long) -> Unit)

    fun stopTimer()

    fun pauseTimer()

}