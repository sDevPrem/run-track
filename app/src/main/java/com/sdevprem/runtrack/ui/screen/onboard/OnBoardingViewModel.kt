package com.sdevprem.runtrack.ui.screen.onboard

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.runtrack.core.data.model.Gender
import com.sdevprem.runtrack.core.data.model.User
import com.sdevprem.runtrack.core.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel(), OnBoardingScreenEvent {
    private val _user = MutableStateFlow(User())
    val user = _user.asStateFlow()

    private val _errorMsg = mutableStateOf("")
    val errorMsg: State<String> = _errorMsg

    init {
        userRepository.user
            .onEach { dbUser -> _user.update { dbUser } }
            .launchIn(viewModelScope)
    }

    private fun User.isUserValid(): Boolean {
        return name.isNotBlank() && weightInKg > 0
    }

    override fun updateName(name: String) = _user.update { it.copy(name = name) }
    override fun updateGender(gender: Gender) = _user.update { it.copy(gender = gender) }
    override fun updateWeight(weightInKg: Float) = _user.update { it.copy(weightInKg = weightInKg) }
    override fun updateWeeklyGoal(weeklyGoalInKm: Float) =
        _user.update { it.copy(weeklyGoalInKM = weeklyGoalInKm) }

    fun saveUser(navigate: () -> Unit) {
        if (!user.value.isUserValid()) {
            _errorMsg.value = "Enter Valid Data"
            return
        }
        viewModelScope.launch {
            userRepository.updateUser(user.value)
            navigate()
        }
    }

    fun resetErrorMsg() {
        _errorMsg.value = ""
    }
}

