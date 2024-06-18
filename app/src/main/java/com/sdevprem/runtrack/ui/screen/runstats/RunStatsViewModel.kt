package com.sdevprem.runtrack.ui.screen.runstats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.runtrack.common.extension.setDateToWeekFirstDay
import com.sdevprem.runtrack.common.extension.setDateToWeekLastDay
import com.sdevprem.runtrack.core.data.repository.AppRepository
import com.sdevprem.runtrack.di.DefaultDispatcher
import com.sdevprem.runtrack.ui.screen.runstats.utils.RunStatsAccumulator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class RunStatsViewModel @Inject constructor(
    private val repository: AppRepository,
    @DefaultDispatcher
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

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

            val todayDate = Calendar.getInstance()
            if (it.dateRange.endInclusive >= todayDate.time) return

            val nextWeekDate = Calendar.getInstance().apply {
                time = it.dateRange.start
                add(Calendar.WEEK_OF_MONTH, 1)
            }
            it.copy(
                dateRange = nextWeekDate.setDateToWeekFirstDay().time..
                        nextWeekDate.setDateToWeekLastDay().time,
            )
        }
        fetchRunInDate()
    }

    fun decrementWeekRange() {
        _state.update {
            val previousWeekDate = Calendar.getInstance().apply {
                time = it.dateRange.start
                add(Calendar.WEEK_OF_MONTH, -1)
            }

            it.copy(
                dateRange = previousWeekDate.setDateToWeekFirstDay().time..
                        previousWeekDate.setDateToWeekLastDay().time,
            )
        }
        fetchRunInDate()
    }

    fun selectStatisticToShow(statistic: RunStatsUiState.Statistic) {
        _state.update { it.copy(statisticToShow = statistic) }
    }
}