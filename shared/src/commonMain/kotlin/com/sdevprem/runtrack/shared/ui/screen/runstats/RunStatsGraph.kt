package com.sdevprem.runtrack.shared.ui.screen.runstats

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDateTime

@Composable
expect fun RunStatsGraphCard(
    runStats: Map<LocalDateTime, RunStatsUiState.AccumulatedRunStatisticsOnDate>,
    dateRange: ClosedRange<LocalDateTime>,
    statisticsToShow: RunStatsUiState.Statistic,
    modifier: Modifier = Modifier,
)