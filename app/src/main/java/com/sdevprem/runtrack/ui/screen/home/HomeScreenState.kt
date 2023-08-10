package com.sdevprem.runtrack.ui.screen.home

import com.sdevprem.runtrack.core.data.model.Run
import com.sdevprem.runtrack.core.data.model.User
import com.sdevprem.runtrack.core.tracking.model.CurrentRunState

data class HomeScreenState(
    val runList: List<Run> = emptyList(),
    val currentRunState: CurrentRunState = CurrentRunState(),
    val currentRunInfo: Run? = null,
    val user: User = User(),
)
