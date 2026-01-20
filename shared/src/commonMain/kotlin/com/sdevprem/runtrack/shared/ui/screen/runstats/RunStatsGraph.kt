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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.multiplatform.cartesian.data.lineSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.LineCartesianLayer.PointConnector
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.common.Fill
import com.patrykandpatrick.vico.multiplatform.common.component.ShapeComponent
import com.patrykandpatrick.vico.multiplatform.common.component.TextComponent
import com.patrykandpatrick.vico.multiplatform.common.data.ExtraStore
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
fun RunStatsGraphCard(
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
    val modelProducer = remember { CartesianChartModelProducer() }
    val dateList = remember(dateRange) {
        (dateRange.start..dateRange.endInclusive).toList()
    }
    val marker = rememberDefaultCartesianMarker(
        indicatorSize = 3.dp,
        label = TextComponent(textStyle = TextStyle.Default.copy(fontSize = 0.sp)),
        indicator = {
            ShapeComponent(
                shape = CircleShape,
                fill = Fill(color = graphPrimaryColor)
            )
        },
    )

    modelProducer.ProduceRunStateModel(
        runStats = runStats,
        extraStoreKey = extraStoreKey,
        dateList = dateList,
        statisticsToShow = statisticsToShow
    )

    val areaFill = remember(graphPrimaryColor) {
        LineCartesianLayer.AreaFill.single(
            fill = Fill(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        graphPrimaryColor.copy(alpha = 0.6f),
                        graphPrimaryColor.copy(alpha = 0.1f)
                    )
                )
            )
        )
    }

    val line = LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(Fill(graphPrimaryColor)),
        pointConnector = PointConnector.cubic(0.5f),
        areaFill = areaFill
    )
    val bottomAxisFormatter = remember {
        CartesianValueFormatter { context, x, _ ->
            context.model.extraStore[extraStoreKey][x.toInt()].let {
                DateUtils.formatDay(it.date)
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    lines = listOf(line)
                )
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = bottomAxisFormatter,
                guideline = null
            ),
            persistentMarkers = {
                dateList.forEachIndexed { i, c ->
                    if (runStats.contains(c)) {
                        marker at i.toFloat()
                    }
                }
            },

            ),
        modelProducer = modelProducer,
        modifier = modifier,

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
            runTransaction {
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
                    extras { it[extraStoreKey] = dateList }
                }
            }
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