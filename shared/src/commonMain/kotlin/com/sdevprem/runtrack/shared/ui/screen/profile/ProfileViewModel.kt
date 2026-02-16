package com.sdevprem.runtrack.shared.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.runtrack.shared.data.repository.AppRepository
import com.sdevprem.runtrack.shared.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import kotlin.math.round

@KoinViewModel
class ProfileViewModel(
    appRepository: AppRepository,
    private val userRepository: UserRepository,
) : ViewModel(), ProfileEditActions {

    private val _profileScreenState = MutableStateFlow(ProfileScreenState())
    val profileScreenState = combine(
        appRepository.getTotalDistance(),
        appRepository.getTotalCaloriesBurned(),
        appRepository.getTotalRunningDuration(),
        _profileScreenState
    ) { distance, calories, duration, state ->
        state.copy(
            totalCaloriesBurnt = calories,
            totalDurationInHr = (round(duration / 36_000.0) / 100.0).toFloat(),
            totalDistanceInKm = distance / 1000f,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ProfileScreenState()
    )

    init {
        userRepository.user
            .onEach { user ->
                _profileScreenState.update { it.copy(user = user) }
            }
            .launchIn(viewModelScope)
    }

    override fun startEditing() = _profileScreenState.update { it.copy(isEditMode = true) }

    override fun saveUser() {
        if (_profileScreenState.value.user.name.isBlank()) {
            _profileScreenState.update { it.copy(errorMsg = "Name can't be empty") }
            return
        }
        viewModelScope.launch {
            userRepository.updateUser(profileScreenState.value.user)
            _profileScreenState.update { it.copy(isEditMode = false) }
        }
    }

    override fun updateUserName(newName: String) {
        _profileScreenState.update { it.copy(user = it.user.copy(name = newName)) }
    }

    override fun updateImgUri(newUri: String?) {
        _profileScreenState.update { it.copy(user = it.user.copy(imgUri = newUri)) }
    }

    override fun cancelEditing() {
        viewModelScope.launch {
            _profileScreenState.update {
                it.copy(
                    user = userRepository.user.first(),
                    isEditMode = false
                )
            }
        }
    }
}