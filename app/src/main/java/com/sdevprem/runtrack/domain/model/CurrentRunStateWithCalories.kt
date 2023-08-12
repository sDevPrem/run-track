package com.sdevprem.runtrack.domain.model

import com.sdevprem.runtrack.core.tracking.model.CurrentRunState

data class CurrentRunStateWithCalories(
    val currentRunState: CurrentRunState = CurrentRunState(),
    val caloriesBurnt: Int = 0
)