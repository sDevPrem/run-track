package com.sdevprem.runtrack.ui.screen.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.runtrack.core.data.repository.AppRepository
import com.sdevprem.runtrack.core.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    appRepository: AppRepository,
    private val userRepository: UserRepository
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
            totalDurationInHr = duration.toBigDecimal()
                .divide((3_600_000).toBigDecimal(), 2, RoundingMode.HALF_UP)
                .toFloat(),
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

    override fun updateImgUri(newUri: Uri?) {
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