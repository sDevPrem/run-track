package com.sdevprem.runtrack.shared

import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.sdevprem.runtrack.shared.ui.screen.currentrun.components.Map

fun MainViewController() = ComposeUIViewController {
    Map(modifier = Modifier, pathPoints = emptyList(), isRunningFinished = false, onSnapshot = {})
}
