package com.sdevprem.runtrack.ui.di

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.runtrack.di.ApplicationScope
import com.sdevprem.runtrack.di.DefaultDispatcher
import com.sdevprem.runtrack.di.IoDispatcher
import com.sdevprem.runtrack.shared.data.repository.AppRepository
import com.sdevprem.runtrack.shared.data.repository.UserRepository
import com.sdevprem.runtrack.shared.domain.tracking.TrackingManager
import com.sdevprem.runtrack.shared.domain.usecase.GetCurrentRunStateWithCaloriesUseCase
import com.sdevprem.runtrack.shared.ui.common.VMProvider
import com.sdevprem.runtrack.shared.ui.screen.currentrun.CurrentRunViewModel
import com.sdevprem.runtrack.shared.ui.screen.home.HomeViewModel
import com.sdevprem.runtrack.shared.ui.screen.onboard.OnBoardingViewModel
import com.sdevprem.runtrack.shared.ui.screen.profile.ProfileViewModel
import com.sdevprem.runtrack.shared.ui.screen.runninghistory.RunningHistoryVM
import com.sdevprem.runtrack.shared.ui.screen.runstats.RunStatsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import kotlin.reflect.KClass

object ViewModelProvider : VMProvider {

    @Composable
    override fun <T : Any> provideViewModel(kClass: KClass<T>): T {
        return when (kClass) {
            RunningHistoryVM::class -> {
                hiltViewModel<AndroidRunningHistoryVM>()
                    .runningHistoryVM as T
            }
            OnBoardingViewModel::class -> {
                hiltViewModel<AndroidOnboardingVM>()
                    .onboardingVM as T
            }
            ProfileViewModel::class -> {
                hiltViewModel<AndroidProfileVM>()
                    .vm as T
            }
            HomeViewModel::class -> {
                hiltViewModel<AndroidHomeVM>()
                    .vm as T
            }
            RunStatsViewModel::class -> {
                hiltViewModel<AndroidRunStatsViewModel>()
                    .runStatsViewModel as T
            }
            CurrentRunViewModel::class -> {
                hiltViewModel<AndroidCurrentRunViewModel>()
                    .viewModel as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${kClass.qualifiedName}")
        }
    }
}

@HiltViewModel
class AndroidRunningHistoryVM @Inject constructor(
    repository: AppRepository,
) : ViewModel() {
    val runningHistoryVM = RunningHistoryVM(
        repository,
        viewModelScope
    )
}

@HiltViewModel
class AndroidOnboardingVM @Inject constructor(
    repository: UserRepository,
) : ViewModel() {
    val onboardingVM = OnBoardingViewModel(
        repository,
        viewModelScope
    )
}

@HiltViewModel
class AndroidProfileVM @Inject constructor(
    appRepository: AppRepository,
    userRepository: UserRepository,
) : ViewModel() {
    val vm = ProfileViewModel(appRepository, userRepository, viewModelScope)
}

@HiltViewModel
class AndroidHomeVM @Inject constructor(
    repository: AppRepository,
    trackingManager: TrackingManager,
    userRepository: UserRepository,
    getCurrentRunStateWithCaloriesUseCase: GetCurrentRunStateWithCaloriesUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationScope private val externalScope: CoroutineScope
) : ViewModel() {
    val vm = HomeViewModel(
        repository,
        trackingManager,
        externalScope,
        ioDispatcher,
        userRepository,
        getCurrentRunStateWithCaloriesUseCase
    )
}

@HiltViewModel
class AndroidRunStatsViewModel @Inject constructor(
    repository: AppRepository,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {
    val runStatsViewModel = RunStatsViewModel(
        repository,
        defaultDispatcher,
        viewModelScope
    )
}

@HiltViewModel
class AndroidCurrentRunViewModel @Inject constructor(
    trackingManager: TrackingManager,
    repository: AppRepository,
    @ApplicationScope appCoroutineScope: CoroutineScope,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
    getCurrentRunStateWithCaloriesUseCase: GetCurrentRunStateWithCaloriesUseCase
) : ViewModel() {
    val viewModel = CurrentRunViewModel(
        trackingManager,
        repository,
        appCoroutineScope,
        ioDispatcher,
        viewModelScope,
        getCurrentRunStateWithCaloriesUseCase
    )
}