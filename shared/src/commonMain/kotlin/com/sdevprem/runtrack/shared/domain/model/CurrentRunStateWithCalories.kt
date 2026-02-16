package com.sdevprem.runtrack.shared.domain.model

import com.sdevprem.runtrack.shared.domain.tracking.model.CurrentRunState

data class CurrentRunStateWithCalories(
    val currentRunState: CurrentRunState = CurrentRunState(),
    val caloriesBurnt: Int = 0
)