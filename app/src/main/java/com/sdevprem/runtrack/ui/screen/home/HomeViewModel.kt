package com.sdevprem.runtrack.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.runtrack.core.data.repository.AppRepository
import com.sdevprem.runtrack.core.data.utils.RunSortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: AppRepository
) : ViewModel() {
    val runList = repository.getSortedAllRun(RunSortOrder.DATE)
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )

}