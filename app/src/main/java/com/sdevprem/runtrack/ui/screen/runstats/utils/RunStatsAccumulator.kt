package com.sdevprem.runtrack.ui.screen.runstats.utils

import com.sdevprem.runtrack.core.data.model.Run
import com.sdevprem.runtrack.ui.screen.runstats.RunStatsUiState.AccumulatedRunStatisticsOnDate
import java.util.Date

object RunStatsAccumulator {

    fun accumulateRunByDate(
        list: List<Run>
    ): Map<Date, AccumulatedRunStatisticsOnDate> {
        return buildMap {
            list.forEach { run ->
                val newStats = AccumulatedRunStatisticsOnDate.fromRun(run)
                this[newStats.date] = newStats + this[newStats.date]
            }
        }
    }

}