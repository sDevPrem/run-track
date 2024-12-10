package com.sdevprem.runtrack.domain.model

import com.sdevprem.runtrack.shared.domain.model.CurrentRunState

data class CurrentRunStateWithCalories(
    val currentRunState: CurrentRunState = CurrentRunState(),
    val caloriesBurnt: Int = 0
)