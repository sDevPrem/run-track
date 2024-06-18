package com.sdevprem.runtrack.ui.screen.runstats

import android.content.res.Configuration
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
import androidx.compose.ui.tooling.preview.Preview
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
import com.sdevprem.runtrack.common.extension.setDateToWeekFirstDay
import com.sdevprem.runtrack.common.extension.setDateToWeekLastDay
import com.sdevprem.runtrack.common.extension.toCalendar
import com.sdevprem.runtrack.common.extension.toList
import com.sdevprem.runtrack.ui.screen.runstats.RunStatsUiState.Statistic.CALORIES
import com.sdevprem.runtrack.ui.screen.runstats.RunStatsUiState.Statistic.DISTANCE
import com.sdevprem.runtrack.ui.screen.runstats.RunStatsUiState.Statistic.DURATION
import com.sdevprem.runtrack.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun RunStatsGraphCard(
    runStats: Map<Date, RunStatsUiState.AccumulatedRunStatisticsOnDate>,
    dateRange: ClosedRange<Date>,
    statisticsToShow: RunStatsUiState.Statistic,
    modifier: Modifier = Modifier,
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
    runStats: Map<Date, RunStatsUiState.AccumulatedRunStatisticsOnDate>,
    dateRange: ClosedRange<Date>,
    statisticsToShow: RunStatsUiState.Statistic,
    modifier: Modifier = Modifier,
) {
    val graphPrimaryColor = MaterialTheme.colorScheme.primary
    val extraStoreKey = remember { ExtraStore.Key<List<Date>>() }
    val modelProducer = remember { CartesianChartModelProducer.build() }
    val dateList = remember(dateRange) {
        (dateRange.start.toCalendar()..dateRange.endInclusive.toCalendar()).toList()
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
                if (runStats.contains(c.time)) {
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
    runStats: Map<Date, RunStatsUiState.AccumulatedRunStatisticsOnDate>,
    extraStoreKey: ExtraStore.Key<List<Date>>,
    dateList: List<Calendar>,
    statisticsToShow: RunStatsUiState.Statistic,
) {
    LaunchedEffect(runStats, extraStoreKey, statisticsToShow) {
        withContext(Dispatchers.Default) {
            tryRunTransaction {
                val y = dateList.map {
                    val currentStats = runStats[it.time] ?: return@map 0
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
                    updateExtras { it[extraStoreKey] = dateList.map { c -> c.time } }
                }
            }
        }
    }
}

@Composable
private fun rememberBottomAxisValueFormatter(
    extraStoreKey: ExtraStore.Key<List<Date>>,
    dateFormatter: SimpleDateFormat = remember { SimpleDateFormat("EEE", Locale.getDefault()) },
) = remember(extraStoreKey) {
    AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, chartValues, _ ->
        chartValues.model.extraStore[extraStoreKey][x.toInt()].let {
            dateFormatter.format(it)
        }
    }
}

@Composable
private fun TopStatistics(
    dateRange: ClosedRange<Date>,
    runStats: Collection<RunStatsUiState.AccumulatedRunStatisticsOnDate>,
    statisticsToShow: RunStatsUiState.Statistic,
) {
    val total by remember(runStats, statisticsToShow) {
        derivedStateOf {
            when (statisticsToShow) {
                CALORIES -> {
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
    val dateFormatter = remember { SimpleDateFormat("MMM dd", Locale.ENGLISH) }
    val formattedDateText = remember(dateRange) {
        dateFormatter.format(dateRange.start) + " - " + dateFormatter.format(dateRange.endInclusive)
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

@Composable
@Preview(
    showBackground = true
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
private fun RunStatsGraphPreview() = AppTheme {
    val from = Calendar.getInstance().setDateToWeekFirstDay()
    val to = Calendar.getInstance().setDateToWeekLastDay()
    val calendar = from.clone() as Calendar

    RunStatsGraphCard(
        runStats = mutableMapOf(
            calendar.time to RunStatsUiState.AccumulatedRunStatisticsOnDate(
                date = calendar.time,
                distanceInMeters = 200
            ),
            calendar.apply {
                add(
                    Calendar.DAY_OF_WEEK,
                    1
                )
            }.time to RunStatsUiState.AccumulatedRunStatisticsOnDate(
                date = calendar.time,
                distanceInMeters = 2000
            ),
            calendar.apply {
                add(
                    Calendar.DAY_OF_WEEK,
                    2
                )
            }.time to RunStatsUiState.AccumulatedRunStatisticsOnDate(
                date = calendar.time,
                distanceInMeters = 2500
            ),
            calendar.apply {
                add(
                    Calendar.DAY_OF_WEEK,
                    1
                )
            }.time to RunStatsUiState.AccumulatedRunStatisticsOnDate(
                date = calendar.time,
                distanceInMeters = 1000
            ),
            calendar.apply {
                add(
                    Calendar.DAY_OF_WEEK,
                    1
                )
            }.time to RunStatsUiState.AccumulatedRunStatisticsOnDate(
                date = calendar.time,
                distanceInMeters = 1000
            ),
        ),
        dateRange = from.time..to.time,
        statisticsToShow = DISTANCE
    )
}

private fun convertMeterToKm(value: Long) = value
    .toBigDecimal()
    .divide(1000.toBigDecimal(), 3, RoundingMode.HALF_DOWN)
    .toFloat()

private fun convertMillisToMinutes(value: Long) = value
    .toBigDecimal()
    .divide(60000.toBigDecimal(), 1, RoundingMode.HALF_DOWN)
    .toFloat()
