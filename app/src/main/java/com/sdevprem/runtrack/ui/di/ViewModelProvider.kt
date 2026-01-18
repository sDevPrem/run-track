package com.sdevprem.runtrack.ui.di

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.runtrack.shared.data.repository.AppRepository
import com.sdevprem.runtrack.shared.data.repository.UserRepository
import com.sdevprem.runtrack.shared.ui.common.VMProvider
import com.sdevprem.runtrack.shared.ui.screen.onboard.OnBoardingViewModel
import com.sdevprem.runtrack.shared.ui.screen.profile.ProfileViewModel
import com.sdevprem.runtrack.shared.ui.screen.runninghistory.RunningHistoryVM
import dagger.hilt.android.lifecycle.HiltViewModel
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