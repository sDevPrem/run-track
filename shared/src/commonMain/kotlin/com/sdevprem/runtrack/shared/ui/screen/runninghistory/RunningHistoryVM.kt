package com.sdevprem.runtrack.shared.ui.screen.runninghistory

import androidx.paging.cachedIn
import com.sdevprem.runtrack.shared.data.model.Run
import com.sdevprem.runtrack.shared.data.repository.AppRepository
import com.sdevprem.runtrack.shared.data.utils.RunSortOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class RunningHistoryVM(
    private val repository: AppRepository,
    private val viewModelScope: CoroutineScope
) {

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