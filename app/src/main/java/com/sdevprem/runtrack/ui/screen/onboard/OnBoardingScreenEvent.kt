package com.sdevprem.runtrack.ui.screen.onboard

import android.net.Uri
import com.sdevprem.runtrack.data.model.Gender

interface OnBoardingScreenEvent {
    fun updateName(name: String)
    fun updateGender(gender: Gender)
    fun updateWeight(weightInKg: Float)
    fun updateWeeklyGoal(weeklyGoalInKm: Float)
    fun updateUserImgUri(uri: Uri?)
}