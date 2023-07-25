package com.sdevprem.runtrack.ui.screen.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.runtrack.core.data.model.Run
import com.sdevprem.runtrack.core.data.repository.AppRepository
import com.sdevprem.runtrack.core.data.repository.UserRepository
import com.sdevprem.runtrack.core.data.utils.RunSortOrder
import com.sdevprem.runtrack.core.tracking.TrackingManager
import com.sdevprem.runtrack.di.ApplicationScope
import com.sdevprem.runtrack.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AppRepository,
    trackingManager: TrackingManager,
    @ApplicationScope
    private val externalScope: CoroutineScope,
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    userRepository: UserRepository
) : ViewModel() {
    val runList = repository.getSortedAllRun(RunSortOrder.DATE)
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )
    val durationInMillis = trackingManager.trackingDurationInMs
    val currentRunState = trackingManager.currentRunState
    val currentRunInfo = mutableStateOf<Run?>(null)
    val doesUserExist = userRepository.doesUserExist
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            null
        )

    fun deleteRun(run: Run) = externalScope.launch(ioDispatcher) {
        repository.deleteRun(run)
    }

}