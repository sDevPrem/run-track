package com.sdevprem.runtrack.shared.ui.screen.runstats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.chart.layout.fullWidth
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.DefaultPointConnector
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.ExtraStore
import com.patrykandpatrick.vico.core.model.lineSeries
import com.sdevprem.runtrack.shared.common.extension.roundTo
import com.sdevprem.runtrack.shared.common.extension.toList
import com.sdevprem.runtrack.shared.common.utils.DateUtils
import com.sdevprem.runtrack.shared.ui.screen.runstats.RunStatsUiState.Statistic.CALORIES
import com.sdevprem.runtrack.shared.ui.screen.runstats.RunStatsUiState.Statistic.DISTANCE
import com.sdevprem.runtrack.shared.ui.screen.runstats.RunStatsUiState.Statistic.DURATION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime

@Composable
actual fun RunStatsGraphCard(
    runStats: Map<LocalDateTime, RunStatsUiState.AccumulatedRunStatisticsOnDate>,
    dateRange: ClosedRange<LocalDateTime>,
    statisticsToShow: RunStatsUiState.Statistic,
    modifier: Modifier
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TopStatistics(
                dateRange = dateRange,
                runStats = runStats.values,
                statisticsToShow = statisticsToShow
            )
            RunStatsGraph(
                runStats = runStats,
                dateRange = dateRange,
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                statisticsToShow = statisticsToShow
            )
        }
    }
}

@Composable
private fun RunStatsGraph(
    runStats: Map<LocalDateTime, RunStatsUiState.AccumulatedRunStatisticsOnDate>,
    dateRange: ClosedRange<LocalDateTime>,
    statisticsToShow: RunStatsUiState.Statistic,
    modifier: Modifier = Modifier,
) {
    val graphPrimaryColor = MaterialTheme.colorScheme.primary
    val extraStoreKey = remember { ExtraStore.Key<List<LocalDateTime>>() }
    val modelProducer = remember { CartesianChartModelProducer.build() }
    val dateList = remember(dateRange) {
        (dateRange.start..dateRange.endInclusive).toList()
    }
    val markerIndicatorSize = LocalDensity.current.run { 3.dp.toPx() }
    val marker = remember(graphPrimaryColor, markerIndicatorSize) {
        MarkerComponent(
            indicator = ShapeComponent(
                shape = Shapes.pillShape,
                color = graphPrimaryColor.toArgb(),
            ),
            label = TextComponent.build { textSizeSp = 0f }
        ).apply { indicatorSizeDp = markerIndicatorSize }
    }
    val markers = remember(runStats, marker) {
        buildMap {
            dateList.forEachIndexed { i, c ->
                if (runStats.contains(c)) {
                    this[i.toFloat()] = marker
                }
            }
        }
    }

    modelProducer.ProduceRunStateModel(
        runStats = runStats,
        extraStoreKey = extraStoreKey,
        dateList = dateList,
        statisticsToShow = statisticsToShow
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lines = listOf(
                    rememberLineSpec(
                        shader = DynamicShaders.color(graphPrimaryColor),
                        pointConnector = DefaultPointConnector(cubicStrength = 0.5f)
                    ),
                ),
            ),
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(
                valueFormatter = rememberBottomAxisValueFormatter(extraStoreKey = extraStoreKey),
                itemPlacer = remember {
                    AxisItemPlacer.Horizontal.default(
                        addExtremeLabelPadding = true,
                    )
                },
                guideline = null,
            ),
            persistentMarkers = markers
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        horizontalLayout = HorizontalLayout.fullWidth(),
    )

}

@Composable
private fun CartesianChartModelProducer.ProduceRunStateModel(
    runStats: Map<LocalDateTime, RunStatsUiState.AccumulatedRunStatisticsOnDate>,
    extraStoreKey: ExtraStore.Key<List<LocalDateTime>>,
    dateList: List<LocalDateTime>,
    statisticsToShow: RunStatsUiState.Statistic,
) {
    LaunchedEffect(runStats, extraStoreKey, statisticsToShow) {
        withContext(Dispatchers.Default) {
            tryRunTransaction {
                val y = dateList.map {
                    val currentStats = runStats[it] ?: return@map 0
                    when (statisticsToShow) {
                        CALORIES -> currentStats.caloriesBurned
                        DURATION -> convertMillisToMinutes(currentStats.durationInMillis)
                        DISTANCE -> convertMeterToKm(
                            currentStats.distanceInMeters.toLong()
                        )
                    }
                }
                lineSeries {
                    series(y)
                    updateExtras { it[extraStoreKey] = dateList }
                }
            }
        }
    }
}

@Composable
private fun rememberBottomAxisValueFormatter(
    extraStoreKey: ExtraStore.Key<List<LocalDateTime>>,
) = remember(extraStoreKey) {
    AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, chartValues, _ ->
        chartValues.model.extraStore[extraStoreKey][x.toInt()].let {
            DateUtils.formatDay(it.date)
        }
    }
}

@Composable
private fun TopStatistics(
    dateRange: ClosedRange<LocalDateTime>,
    runStats: Collection<RunStatsUiState.AccumulatedRunStatisticsOnDate>,
    statisticsToShow: RunStatsUiState.Statistic,
) {
    val total by remember(runStats, statisticsToShow) {
        derivedStateOf {
            when (statisticsToShow) {
                RunStatsUiState.Statistic.CALORIES -> {
                    runStats.sumOf { it.caloriesBurned }.toString()
                }

                DURATION -> {
                    val totalInMeters = runStats.sumOf { it.durationInMillis }
                    convertMillisToMinutes(totalInMeters).toString()
                }

                DISTANCE -> {
                    val totalMillis = runStats.sumOf { it.distanceInMeters }
                    convertMeterToKm(totalMillis.toLong()).toString()
                }
            }
        }
    }
    val formattedDateText = remember(dateRange) {
        DateUtils.formatDay(dateRange.start.date) + " - " + DateUtils.formatDay(dateRange.endInclusive.date)
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.onSurface
                )
                .width(2.dp)
                .fillMaxHeight()
        )
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = total,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.alignByBaseline()
                )
                Text(
                    text = when (statisticsToShow) {
                        CALORIES -> "kcal"
                        DURATION -> "min"
                        DISTANCE -> "km"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.alignByBaseline()
                )
            }
            Text(
                text = formattedDateText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun convertMeterToKm(value: Long): Float {
    return (value / 1000f).roundTo(3)
}

private fun convertMillisToMinutes(value: Long): Float {
    return (value / 60000f).roundTo(1)
}