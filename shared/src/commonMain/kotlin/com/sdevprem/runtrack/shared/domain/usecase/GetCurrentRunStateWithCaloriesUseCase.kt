package com.sdevprem.runtrack.shared.domain.usecase

import com.sdevprem.runtrack.shared.data.repository.UserRepository
import com.sdevprem.runtrack.shared.domain.model.CurrentRunStateWithCalories
import com.sdevprem.runtrack.shared.domain.tracking.TrackingManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.math.roundToInt

class GetCurrentRunStateWithCaloriesUseCase(
    private val userRepository: UserRepository,
    private val trackingManager: TrackingManager
) {
    operator fun invoke(): Flow<CurrentRunStateWithCalories> {
        return combine(userRepository.user, trackingManager.currentRunState) { user, runState ->
            CurrentRunStateWithCalories(
                currentRunState = runState,
                caloriesBurnt = calculateCaloriesBurnt(
                    distanceInMeters = runState.distanceInMeters,
                    weightInKg = user.weightInKg
                ).roundToInt()
            )
        }
    }

    //from chat gpt
    private fun calculateCaloriesBurnt(distanceInMeters: Int, weightInKg: Float) =
        (0.75f * weightInKg) * (distanceInMeters / 1000f)
}