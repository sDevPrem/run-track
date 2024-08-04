package com.sdevprem.runtrack.domain.model

import com.sdevprem.runtrack.domain.tracking.model.CurrentRunState

data class CurrentRunStateWithCalories(
    val currentRunState: CurrentRunState = CurrentRunState(),
    val caloriesBurnt: Int = 0
)