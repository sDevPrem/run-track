package com.sdevprem.runtrack.shared.ui.screen.runstats

import com.sdevprem.runtrack.shared.common.extension.now
import com.sdevprem.runtrack.shared.common.extension.toWeekFirstDay
import com.sdevprem.runtrack.shared.data.repository.AppRepository
import com.sdevprem.runtrack.shared.ui.screen.runstats.utils.RunStatsAccumulator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atTime
import kotlinx.datetime.plus

class RunStatsViewModel(
    private val repository: AppRepository,
    private val defaultDispatcher: CoroutineDispatcher,
    private val viewModelScope: CoroutineScope
) {

    private val _state = MutableStateFlow(RunStatsUiState.EMPTY_STATE)
    val state = _state.asStateFlow()

    init {
        fetchRunInDate()
    }

    private fun fetchRunInDate() = viewModelScope.launch {
        val runList = state.value.let {
            repository.getRunStatsInDateRange(
                fromDate = it.dateRange.start,
                toDate = it.dateRange.endInclusive
            )
        }
        withContext(defaultDispatcher) {
            _state.update {
                it.copy(
                    runStats = runList,
                    runStatisticsOnDate = RunStatsAccumulator.accumulateRunByDate(runList)
                )
            }
        }
    }

    fun incrementWeekRange() {
        _state.update {

            val todayDate = LocalDateTime.now()
            if (it.dateRange.endInclusive >= todayDate) return

            val nextWeekDate = it.dateRange.start.date.plus(DatePeriod(days = 7))
                .atTime(0, 0, 0)
            it.copy(
                dateRange = nextWeekDate.toWeekFirstDay()..
                        nextWeekDate.toWeekFirstDay(),
            )
        }
        fetchRunInDate()
    }

    fun decrementWeekRange() {
        _state.update {
            val previousWeekDate = it.dateRange.start.date.plus(DatePeriod(days = -7))
                .atTime(0, 0, 0)

            it.copy(
                dateRange = previousWeekDate.toWeekFirstDay()..
                        previousWeekDate.toWeekFirstDay(),
            )
        }
        fetchRunInDate()
    }

    fun selectStatisticToShow(statistic: RunStatsUiState.Statistic) {
        _state.update { it.copy(statisticToShow = statistic) }
    }
}