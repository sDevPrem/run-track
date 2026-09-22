package com.sdevprem.runtrack.shared.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.runtrack.shared.common.extension.now
import com.sdevprem.runtrack.shared.common.extension.toWeekFirstDay
import com.sdevprem.runtrack.shared.common.extension.toWeekLastDay
import com.sdevprem.runtrack.shared.data.model.Run
import com.sdevprem.runtrack.shared.data.repository.AppRepository
import com.sdevprem.runtrack.shared.data.repository.UserRepository
import com.sdevprem.runtrack.shared.di.CoroutineDispatchers
import com.sdevprem.runtrack.shared.domain.tracking.TrackingManager
import com.sdevprem.runtrack.shared.domain.usecase.GetCurrentRunStateWithCaloriesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Named

@KoinViewModel
class HomeViewModel(
    private val repository: AppRepository,
    trackingManager: TrackingManager,
    @Named("ApplicationScope") private val externalScope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    userRepository: UserRepository,
    getCurrentRunStateWithCaloriesUseCase: GetCurrentRunStateWithCaloriesUseCase
) : ViewModel() {

    val durationInMillis = trackingManager.trackingDurationInMs

    val doesUserExist = userRepository.doesUserExist
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            null
        )

    private val calendar = LocalDateTime.now()

    private val distanceCoveredInThisWeekInMeter = repository.getTotalDistance(
        calendar.toWeekFirstDay(),
        calendar.toWeekLastDay()
    )

    private val _homeScreenState = MutableStateFlow(HomeScreenState())
    val homeScreenState = combine(
        repository.getRunByDescDateWithLimit(3),
        getCurrentRunStateWithCaloriesUseCase(),
        userRepository.user,
        distanceCoveredInThisWeekInMeter,
        _homeScreenState,
    ) { runList, runState, user, distanceInMeter, state ->
        state.copy(
            runList = runList.map { it },
            currentRunStateWithCalories = runState,
            user = user,
            distanceCoveredInKmInThisWeek = distanceInMeter / 1000f
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        HomeScreenState()
    )

    fun deleteRun(run: Run) = externalScope.launch(dispatchers.io) {
        dismissRunDialog()
        repository.deleteRun(run)
    }

    fun showRun(run: Run) {
        _homeScreenState.update { it.copy(currentRunInfo = run) }
    }

    fun dismissRunDialog() {
        _homeScreenState.update { it.copy(currentRunInfo = null) }
    }

}