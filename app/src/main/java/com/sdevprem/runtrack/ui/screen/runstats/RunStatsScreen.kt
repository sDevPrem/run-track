package com.sdevprem.runtrack.ui.screen.runstats

import android.content.res.Configuration
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sdevprem.runtrack.R
import com.sdevprem.runtrack.core.data.model.Run
import com.sdevprem.runtrack.ui.theme.AppTheme

@Composable
fun RunStatsScreen(
    viewModel: RunStatsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RunStatsContent(state = state)
}

@Composable
private fun RunStatsContent(
    state: RunStatsUiState
) {
    Column {
        RunStatsGraph(
            runStats = state.runStatisticsOnDate,
            dateRange = state.dateRange,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )
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
private fun RunStatsScreenPreview() = AppTheme {
    val previewState = RunStatsUiState.EMPTY_STATE.copy(
        runStats = listOf(
            getDemoRun().copy()
        )
    )
    RunStatsContent(
        previewState
    )
}

@Composable
private fun getDemoRun() = Run(
    img = BitmapFactory.decodeResource(
        LocalContext.current.resources,
        R.drawable.running_boy
    )
)