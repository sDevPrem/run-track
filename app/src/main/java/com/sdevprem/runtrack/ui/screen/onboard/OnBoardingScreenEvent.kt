package com.sdevprem.runtrack.ui.screen.onboard

import com.sdevprem.runtrack.core.data.model.Gender

interface OnBoardingScreenEvent {
    fun updateName(name: String)
    fun updateGender(gender: Gender)
    fun updateWeight(weightInKg: Float)
    fun updateWeeklyGoal(weeklyGoalInKm: Float)
}