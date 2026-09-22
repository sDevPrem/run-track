package com.sdevprem.runtrack.shared.ui.screen.currentrun.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sdevprem.runtrack.shared.domain.tracking.model.PathPoint

@Composable
expect fun Map(
    modifier: Modifier = Modifier,
    pathPoints: List<PathPoint>,
    isRunningFinished: Boolean,
    onSnapshot: (ByteArray) -> Unit,
)