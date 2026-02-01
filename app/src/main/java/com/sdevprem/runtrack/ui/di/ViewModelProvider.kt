package com.sdevprem.runtrack.ui.di

import androidx.compose.runtime.Composable
import com.sdevprem.runtrack.shared.ui.common.VMProvider
import kotlin.reflect.KClass

object ViewModelProvider : VMProvider {

    @Composable
    override fun <T : Any> provideViewModel(kClass: KClass<T>): T {
//        return when (kClass) {
//            RunningHistoryVM::class -> {
//                hiltViewModel<AndroidRunningHistoryVM>()
//                    .runningHistoryVM as T
//            }
//            OnBoardingViewModel::class -> {
//                hiltViewModel<AndroidOnboardingVM>()
//                    .onboardingVM as T
//            }
//            ProfileViewModel::class -> {
//                hiltViewModel<AndroidProfileVM>()
//                    .vm as T
//            }
//            HomeViewModel::class -> {
//                hiltViewModel<AndroidHomeVM>()
//                    .vm as T
//            }
//            RunStatsViewModel::class -> {
//                hiltViewModel<AndroidRunStatsViewModel>()
//                    .runStatsViewModel as T
//            }
//            CurrentRunViewModel::class -> {
//                hiltViewModel<AndroidCurrentRunViewModel>()
//                    .viewModel as T
//            }
////            MainScreenViewModel::class -> {
////                hiltViewModel<AndroidMainScreenViewModel>()
////                    .vm as T
////            }
//
//            else -> throw IllegalArgumentException("Unknown ViewModel class: ${kClass.qualifiedName}")
//        }
        TODO()
    }
}
//
//@HiltViewModel
//class AndroidRunningHistoryVM @Inject constructor(
//    repository: AppRepository,
//) : ViewModel() {
//    val runningHistoryVM = RunningHistoryVM(
//        repository,
//        viewModelScope
//    )
//}
//
//@HiltViewModel
//class AndroidOnboardingVM @Inject constructor(
//    repository: UserRepository,
//) : ViewModel() {
//    val onboardingVM = OnBoardingViewModel(
//        repository,
//        viewModelScope
//    )
//}
//
//@HiltViewModel
//class AndroidProfileVM @Inject constructor(
//    appRepository: AppRepository,
//    userRepository: UserRepository,
//) : ViewModel() {
//    val vm = ProfileViewModel(appRepository, userRepository, viewModelScope)
//}
//
//@HiltViewModel
//class AndroidHomeVM @Inject constructor(
//    repository: AppRepository,
//    trackingManager: TrackingManager,
//    userRepository: UserRepository,
//    getCurrentRunStateWithCaloriesUseCase: GetCurrentRunStateWithCaloriesUseCase,
//    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
//    @ApplicationScope private val externalScope: CoroutineScope
//) : ViewModel() {
//    val vm = HomeViewModel(
//        repository,
//        trackingManager,
//        externalScope,
//        ioDispatcher,
//        userRepository,
//        getCurrentRunStateWithCaloriesUseCase
//    )
//}
//
//@HiltViewModel
//class AndroidRunStatsViewModel @Inject constructor(
//    repository: AppRepository,
//    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
//) : ViewModel() {
//    val runStatsViewModel = RunStatsViewModel(
//        repository,
//        defaultDispatcher,
//        viewModelScope
//    )
//}
//
//@HiltViewModel
//class AndroidCurrentRunViewModel @Inject constructor(
//    trackingManager: TrackingManager,
//    repository: AppRepository,
//    @ApplicationScope appCoroutineScope: CoroutineScope,
//    @IoDispatcher ioDispatcher: CoroutineDispatcher,
//    getCurrentRunStateWithCaloriesUseCase: GetCurrentRunStateWithCaloriesUseCase
//) : ViewModel() {
//    val viewModel = CurrentRunViewModel(
//        trackingManager,
//        repository,
//        appCoroutineScope,
//        ioDispatcher,
//        viewModelScope,
//        getCurrentRunStateWithCaloriesUseCase
//    )
//}
//
//@HiltViewModel
//class AndroidMainScreenViewModel @Inject constructor(
//    userRepository: UserRepository,
//) : ViewModel() {
//    val vm = MainScreenViewModel(
//        userRepository,
////        viewModelScope
//    )
//}