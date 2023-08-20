package com.sdevprem.runtrack.ui.screen.runninghistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sdevprem.runtrack.core.data.model.Run
import com.sdevprem.runtrack.core.data.repository.AppRepository
import com.sdevprem.runtrack.core.data.utils.RunSortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class RunningHistoryVM @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _runSortOrder = MutableStateFlow(RunSortOrder.DATE)

    val runList = _runSortOrder.flatMapLatest {
        repository.getSortedAllRun(it)
            .flow
            .cachedIn(viewModelScope)
    }

    private val _dialogRun = MutableStateFlow<Run?>(null)
    val dialogRun = _dialogRun.asStateFlow()

    fun setSortOrder(sortOrder: RunSortOrder) {
        _runSortOrder.value = sortOrder
    }

    fun setDialogRun(run: Run?) {
        _dialogRun.value = run
    }

    fun deleteRun() = dialogRun.value?.let {
        viewModelScope.launch {
            _dialogRun.value = null
            repository.deleteRun(it)
        }
    }
}