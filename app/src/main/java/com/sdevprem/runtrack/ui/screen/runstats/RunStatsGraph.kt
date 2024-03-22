package com.sdevprem.runtrack.ui.screen.runstats

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import com.sdevprem.runtrack.ui.theme.AppTheme
import com.sdevprem.runtrack.utils.setDateToWeekFirstDay
import com.sdevprem.runtrack.utils.setDateToWeekLastDay
import com.sdevprem.runtrack.utils.toCalendar
import com.sdevprem.runtrack.utils.toList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun RunStatsGraph(
    runStats: Map<Date, RunStatsUiState.AccumulatedRunStatisticsOnDate>,
    dateRange: ClosedRange<Date>,
    modifier: Modifier = Modifier,
) {
    val graphPrimaryColor = MaterialTheme.colorScheme.primary
    val extraStoreKey = remember { ExtraStore.Key<List<Date>>() }
    val modelProducer = remember { CartesianChartModelProducer.build() }
    val dateList = remember(dateRange) {
        (dateRange.start.toCalendar()..dateRange.endInclusive.toCalendar()).toList()
    }
    val marker = remember(graphPrimaryColor) { createMarker(color = graphPrimaryColor) }
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
        dateList = dateList
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
                        addExtremeLabelPadding = true
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
) {
    LaunchedEffect(runStats, extraStoreKey) {
        withContext(Dispatchers.Default) {
            tryRunTransaction {
                val y = dateList.map {
                    runStats[it.time]?.distanceInMeters ?: 0
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

private fun createMarker(
    color: Color,
    strokeColor: Color = color,
) = object : MarkerComponent(
    indicator = ShapeComponent(
        shape = Shapes.pillShape,
        color = color.toArgb(),
        strokeColor = strokeColor.toArgb()
    ),
    label = TextComponent.build { this.textSizeSp = 0f }
) {
    init {
        indicatorSizeDp = 8.dp.value
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

    RunStatsGraph(
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
        dateRange = from.time..to.time
    )
}

