package com.sdevprem.runtrack.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.runtrack.common.extension.setDateToWeekFirstDay
import com.sdevprem.runtrack.common.extension.setDateToWeekLastDay
import com.sdevprem.runtrack.data.db.mapper.toDataModel
import com.sdevprem.runtrack.data.db.mapper.toDateTime
import com.sdevprem.runtrack.data.db.mapper.toEntity
import com.sdevprem.runtrack.data.model.Run
import com.sdevprem.runtrack.di.ApplicationScope
import com.sdevprem.runtrack.di.IoDispatcher
import com.sdevprem.runtrack.domain.usecase.GetCurrentRunStateWithCaloriesUseCase
import com.sdevprem.runtrack.shared.data.repository.AppRepository
import com.sdevprem.runtrack.shared.data.repository.UserRepository
import com.sdevprem.runtrack.shared.domain.tracking.TrackingManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AppRepository,
    trackingManager: TrackingManager,
    @ApplicationScope
    private val externalScope: CoroutineScope,
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
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

    private val calendar = Calendar.getInstance()

    private val distanceCoveredInThisWeekInMeter = repository.getTotalDistance(
        calendar.setDateToWeekFirstDay().time.toDateTime(),
        calendar.setDateToWeekLastDay().time.toDateTime()
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
            runList = runList.map { it.toDataModel() },
            currentRunStateWithCalories = runState,
            user = user,
            distanceCoveredInKmInThisWeek = distanceInMeter / 1000f
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        HomeScreenState()
    )

    fun deleteRun(run: Run) = externalScope.launch(ioDispatcher) {
        dismissRunDialog()
        repository.deleteRun(run.toEntity())
    }

    fun showRun(run: Run) {
        _homeScreenState.update { it.copy(currentRunInfo = run) }
    }

    fun dismissRunDialog() {
        _homeScreenState.update { it.copy(currentRunInfo = null) }
    }

}