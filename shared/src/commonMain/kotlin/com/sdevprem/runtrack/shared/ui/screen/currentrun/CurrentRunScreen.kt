package com.sdevprem.runtrack.shared.ui.screen.currentrun

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sdevprem.runtrack.shared.ui.common.LocalVMProvider
import com.sdevprem.runtrack.shared.ui.common.common.animation.ComposeUtils
import com.sdevprem.runtrack.shared.ui.screen.currentrun.components.CurrentRunStatsCard
import com.sdevprem.runtrack.shared.ui.screen.currentrun.components.Map
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.vectorResource
import runtrack.shared.generated.resources.Res
import runtrack.shared.generated.resources.ic_back

//@Composable
//@Preview(showBackground = true)
//private fun CurrentRunComposable() {
//    AppTheme {
//        Surface {
//            CurrentRunScreen(rememberNavController())
//        }
//    }
//}

@Composable
fun CurrentRunScreen(
    navigateUp: () -> Unit,
    viewModel: CurrentRunViewModel = LocalVMProvider.current
        .provideViewModel(CurrentRunViewModel::class)
) {
//    val context = LocalContext.current
//
//    LaunchedEffect(key1 = true) {
//        LocationUtils.checkAndRequestLocationSetting(context as Activity)
//    }
    var isRunningFinished by rememberSaveable { mutableStateOf(false) }
    var shouldShowRunningCard by rememberSaveable { mutableStateOf(false) }
    val runState by viewModel.currentRunStateWithCalories.collectAsStateWithLifecycle()
    val runningDurationInMillis by viewModel.runningDurationInMillis.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        delay(ComposeUtils.slideDownInDuration + 200L)
        shouldShowRunningCard = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Map(
            pathPoints = runState.currentRunState.pathPoints,
            isRunningFinished = isRunningFinished,
        ) {
            viewModel.finishRun(it)
            navigateUp()
        }
        TopBar(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp),
            onNavigateUp = navigateUp
        )
        ComposeUtils.SlideUpAnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            visible = shouldShowRunningCard
        ) {
            CurrentRunStatsCard(
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                onPlayPauseButtonClick = viewModel::playPauseTracking,
                runState = runState,
                durationInMillis = runningDurationInMillis,
                onFinish = { isRunningFinished = true }
            )
        }

    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit
) {
    IconButton(
        onClick = onNavigateUp,
        modifier = modifier
            .size(32.dp)
            .shadow(
                elevation = 4.dp,
                shape = MaterialTheme.shapes.medium,
                clip = true
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
            )
            .padding(4.dp)
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_back),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}
