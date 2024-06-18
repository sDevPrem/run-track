package com.sdevprem.runtrack.domain.usecase

import com.sdevprem.runtrack.common.utils.RunUtils
import com.sdevprem.runtrack.core.data.repository.UserRepository
import com.sdevprem.runtrack.core.tracking.TrackingManager
import com.sdevprem.runtrack.domain.model.CurrentRunStateWithCalories
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class GetCurrentRunStateWithCaloriesUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val trackingManager: TrackingManager
) {
    operator fun invoke(): Flow<CurrentRunStateWithCalories> {
        return combine(userRepository.user, trackingManager.currentRunState) { user, runState ->
            CurrentRunStateWithCalories(
                currentRunState = runState,
                caloriesBurnt = RunUtils.calculateCaloriesBurnt(
                    distanceInMeters = runState.distanceInMeters,
                    weightInKg = user.weightInKg
                ).roundToInt()
            )
        }
    }
}