package com.sdevprem.runtrack.ui.screen.home

import com.sdevprem.runtrack.data.model.Run
import com.sdevprem.runtrack.domain.model.CurrentRunStateWithCalories
import com.sdevprem.runtrack.shared.data.model.User

data class HomeScreenState(
    val runList: List<Run> = emptyList(),
    val currentRunStateWithCalories: CurrentRunStateWithCalories = CurrentRunStateWithCalories(),
    val currentRunInfo: Run? = null,
    val user: User = User(),
    val distanceCoveredInKmInThisWeek: Float = 0.0f
)
