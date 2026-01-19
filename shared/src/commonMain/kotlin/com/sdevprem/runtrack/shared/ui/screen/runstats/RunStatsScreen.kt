package com.sdevprem.runtrack.shared.ui.screen.runstats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sdevprem.runtrack.shared.common.extension.now
import com.sdevprem.runtrack.shared.common.extension.toList
import com.sdevprem.runtrack.shared.common.utils.DateUtils
import com.sdevprem.runtrack.shared.ui.common.LocalVMProvider
import com.sdevprem.runtrack.shared.ui.common.extension.conditional
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Composable
fun RunStatsScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RunStatsViewModel = LocalVMProvider.current.provideViewModel(RunStatsViewModel::class),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RunStatsContent(
        state = state,
        modifier = modifier,
        onStatisticSelected = viewModel::selectStatisticToShow,
        decrementDateRange = viewModel::decrementWeekRange,
        incrementDateRange = viewModel::incrementWeekRange,
        navigateUp = navigateUp
    )
}

@Composable
private fun RunStatsContent(
    state: RunStatsUiState,
    onStatisticSelected: (RunStatsUiState.Statistic) -> Unit,
    incrementDateRange: () -> Unit,
    decrementDateRange: () -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateList = remember(state.dateRange) {
        (state.dateRange.start..state.dateRange.endInclusive).toList()
    }
    Scaffold(
        topBar = { TopBar(navigateUp = navigateUp) },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Weekly Report",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(bottom = 8.dp)
            )
            DateRangeCard(
                dateList = dateList,
                isDataAvailable = { state.runStatisticsOnDate.containsKey(it) },
                incrementDateRange = incrementDateRange,
                decrementDateRange = decrementDateRange
            )
            StatisticFilter(
                selectedStatistic = state.statisticToShow,
                onSelectChip = onStatisticSelected
            )
            RunStatsGraphCard(
                runStats = state.runStatisticsOnDate,
                dateRange = state.dateRange,
                modifier = Modifier
                    .fillMaxWidth(),
                statisticsToShow = state.statisticToShow
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(navigateUp: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = "Statistics")
        },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back"
                )
            }
        }
    )
}

@Composable
private fun StatisticFilter(
    selectedStatistic: RunStatsUiState.Statistic,
    onSelectChip: (RunStatsUiState.Statistic) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RunStatsUiState.Statistic.entries.forEach {
            ElevatedFilterChip(
                onClick = { onSelectChip(it) },
                selected = it == selectedStatistic,
                leadingIcon = {
                    AnimatedVisibility(it == selectedStatistic) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "selected Filter",
                        )
                    }
                },
                label = {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }
    }
}

@Composable
private fun DateRangeCard(
    dateList: List<LocalDateTime>,
    isDataAvailable: (date: LocalDateTime) -> Boolean,
    decrementDateRange: () -> Unit,
    incrementDateRange: () -> Unit,
) {
    val todayDate = remember(dateList) {
        LocalDateTime.now()
    }
    val canIncrementDate = remember(dateList, todayDate) {
        dateList.last() < todayDate
    }
    ElevatedCard(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = decrementDateRange) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "previous week",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            dateList.forEach {
                DateRangeColumn(
                    date = it.date,
                    isDataAvailable = isDataAvailable(it),
                    isTodayDate = it == todayDate
                )
            }
            IconButton(
                onClick = incrementDateRange,
                enabled = canIncrementDate
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "next week",
                    tint = if (canIncrementDate)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else LocalContentColor.current
                )
            }
        }
    }
}

@Composable
private fun DateRangeColumn(
    date: LocalDate,
    isDataAvailable: Boolean,
    isTodayDate: Boolean,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = DateUtils.formatDay(date),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = DateUtils.formatDD(date),
            style = MaterialTheme.typography.labelLarge,
            color = if (isDataAvailable) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier
                .conditional(
                    condition = isDataAvailable || isTodayDate,
                ) {
                    Modifier
                        .drawBehind {
                            if (isDataAvailable) {
                                drawCircle(
                                    color = primaryColor,
                                    radius = size.maxDimension / 2
                                )
                            }
                            if (isTodayDate) {
                                drawCircle(
                                    color = onSurfaceColor,
                                    radius = size.maxDimension / 2,
                                    style = Stroke(
                                        width = 2.dp.toPx()
                                    )
                                )
                            }

                        }
                }
                .padding(4.dp)
        )
    }
}

//@Composable
//@Preview(
//    showBackground = true
//)
//@Preview(
//    showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_YES
//)
//private fun RunStatsScreenPreview() = AppTheme {
//    val img = BitmapFactory.decodeResource(
//        LocalContext.current.resources,
//        R.drawable.running_boy
//    )
//    var previewState by remember {
//        val runList = getDemoRunList(img)
//        mutableStateOf(
//            RunStatsUiState.EMPTY_STATE.copy(
//                runStats = runList,
//                runStatisticsOnDate = RunStatsAccumulator.accumulateRunByDate(runList)
//            )
//        )
//    }
//    RunStatsContent(
//        previewState,
//        onStatisticSelected = {
//            previewState = previewState.copy(statisticToShow = it)
//        },
//        decrementDateRange = {},
//        incrementDateRange = {},
//        navigateUp = {}
//    )
//}
//
//private fun getDemoRun(img: Bitmap) = Run(
//    img = img,
//    distanceInMeters = 1000,
//)
//
//private fun getDemoRunList(img: Bitmap) = buildList {
//    val dateList = (Calendar.getInstance().setDateToWeekFirstDay()..Calendar.getInstance()
//        .setDateToWeekLastDay()).toList()
//    dateList.forEach {
//        add(
//            getDemoRun(img).copy(
//                timestamp = it.time,
//                distanceInMeters = Random.nextInt(200, 1000)
//            )
//        )
//    }
//}