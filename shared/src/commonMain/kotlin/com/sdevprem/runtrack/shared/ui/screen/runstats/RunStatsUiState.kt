package com.sdevprem.runtrack.shared.ui.screen.runstats

import androidx.compose.runtime.Immutable
import com.sdevprem.runtrack.shared.common.extension.now
import com.sdevprem.runtrack.shared.common.extension.setMinimumTime
import com.sdevprem.runtrack.shared.common.extension.toWeekFirstDay
import com.sdevprem.runtrack.shared.common.extension.toWeekLastDay
import com.sdevprem.runtrack.shared.data.model.Run
import kotlinx.datetime.LocalDateTime

@Immutable
data class RunStatsUiState(
    val dateRange: ClosedRange<LocalDateTime>,
    val runStats: List<Run>,
    val statisticToShow: Statistic,
    val runStatisticsOnDate: Map<LocalDateTime, AccumulatedRunStatisticsOnDate>,
) {

    data class AccumulatedRunStatisticsOnDate(
        val date: LocalDateTime = LocalDateTime.Companion.now(),
        val distanceInMeters: Int = 0,
        val durationInMillis: Long = 0L,
        val caloriesBurned: Int = 0
    ) {
        operator fun plus(other: AccumulatedRunStatisticsOnDate?) = other?.let {
            AccumulatedRunStatisticsOnDate(
                date = this.date,
                distanceInMeters = this.distanceInMeters + other.distanceInMeters,
                durationInMillis = this.durationInMillis + other.durationInMillis,
                caloriesBurned = this.caloriesBurned + other.caloriesBurned
            )
        } ?: this

        companion object {
            fun fromRun(run: Run) = AccumulatedRunStatisticsOnDate(
                date = run.timestamp.setMinimumTime(),
                distanceInMeters = run.distanceInMeters,
                durationInMillis = run.durationInMillis,
                caloriesBurned = run.caloriesBurned
            )
        }
    }

    enum class Statistic {
        CALORIES,
        DURATION,
        DISTANCE
    }

    companion object {
        val EMPTY_STATE
            get() = RunStatsUiState(
                dateRange = LocalDateTime.Companion.now().toWeekFirstDay()..
                        LocalDateTime.Companion.now().toWeekLastDay(),
                runStats = emptyList(),
                statisticToShow = Statistic.DISTANCE,
                runStatisticsOnDate = emptyMap()
            )
    }
}