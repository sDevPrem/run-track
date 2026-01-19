package com.sdevprem.runtrack.shared.ui.screen.runstats.utils

import com.sdevprem.runtrack.shared.data.model.Run
import com.sdevprem.runtrack.shared.ui.screen.runstats.RunStatsUiState.AccumulatedRunStatisticsOnDate
import kotlinx.datetime.LocalDateTime

object RunStatsAccumulator {

    fun accumulateRunByDate(
        list: List<Run>
    ): Map<LocalDateTime, AccumulatedRunStatisticsOnDate> {
        return buildMap {
            list.forEach { run ->
                val newStats = AccumulatedRunStatisticsOnDate.fromRun(run)
                this[newStats.date] = newStats + this[newStats.date]
            }
        }
    }

}