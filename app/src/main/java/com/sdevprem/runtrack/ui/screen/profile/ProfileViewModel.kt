package com.sdevprem.runtrack.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.runtrack.core.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    appRepository: AppRepository
) : ViewModel() {
    val profileScreenState = combine(
        appRepository.getTotalDistance(),
        appRepository.getTotalCaloriesBurned(),
        appRepository.getTotalRunningDuration()
    ) { distance, calories, duration ->
        ProfileScreenState(
            totalCaloriesBurnt = calories,
            totalDurationInHr = duration.toBigDecimal()
                .divide((3_600_000).toBigDecimal(), 2, RoundingMode.HALF_UP)
                .toFloat(),
            totalDistanceInKm = distance / 1000f
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ProfileScreenState()
    )

}