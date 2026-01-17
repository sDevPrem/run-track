package com.sdevprem.runtrack.ui.di

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.runtrack.shared.data.repository.AppRepository
import com.sdevprem.runtrack.shared.ui.common.VMProvider
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