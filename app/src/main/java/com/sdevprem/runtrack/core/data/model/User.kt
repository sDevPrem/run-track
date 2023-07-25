package com.sdevprem.runtrack.core.data.model

data class User(
    val name: String = "",
    val gender: Gender = Gender.MALE,
    val weightInKg: Float = 0.0f,
    val weeklyGoalInKM: Float = 0.0f
)
