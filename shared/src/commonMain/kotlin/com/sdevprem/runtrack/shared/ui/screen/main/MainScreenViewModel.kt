package com.sdevprem.runtrack.shared.ui.screen.main

import com.sdevprem.runtrack.shared.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MainScreenViewModel(
    userRepository: UserRepository,
    viewModelScope: CoroutineScope
) {
    val doesUserExist = userRepository.doesUserExist
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            null
        )
}