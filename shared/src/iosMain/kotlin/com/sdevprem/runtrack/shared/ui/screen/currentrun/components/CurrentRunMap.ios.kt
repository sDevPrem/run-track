package com.sdevprem.runtrack.shared.ui.screen.currentrun.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sdevprem.runtrack.shared.domain.tracking.model.PathPoint

@Composable
actual fun Map(
    modifier: Modifier,
    pathPoints: List<PathPoint>,
    isRunningFinished: Boolean,
    onSnapshot: (ByteArray) -> Unit
) {
    TODO("Not yet implemented")
}