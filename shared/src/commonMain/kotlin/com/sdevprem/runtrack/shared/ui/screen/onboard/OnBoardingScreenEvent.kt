package com.sdevprem.runtrack.shared.ui.screen.onboard

import com.sdevprem.runtrack.shared.data.model.Gender

interface OnBoardingScreenEvent {
    fun updateName(name: String)
    fun updateGender(gender: Gender)
    fun updateWeight(weightInKg: Float)
    fun updateWeeklyGoal(weeklyGoalInKm: Float)
}