package com.sdevprem.runtrack.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sdevprem.runtrack.core.data.model.Gender
import com.sdevprem.runtrack.core.data.model.User
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_GENDER = stringPreferencesKey("user_gender")
        val USER_WEIGHT_IN_KG = floatPreferencesKey("user_weight_in_kg")
        val USER_WEEKLY_GOAL_IN_KM = floatPreferencesKey("user_weekly_goal_in_km")
    }

    val user = dataStore.data.map {
        User(
            name = it[USER_NAME] ?: "",
            gender = Gender.valueOf(it[USER_GENDER] ?: Gender.MALE.name),
            weightInKg = it[USER_WEIGHT_IN_KG] ?: 0.0f,
            weeklyGoalInKM = it[USER_WEEKLY_GOAL_IN_KM] ?: 0.0f,
        )
    }

    val doesUserExist = dataStore.data.map {
        it[USER_NAME] != null
    }

    suspend fun updateUser(user: User) = dataStore.edit {
        it[USER_NAME] = user.name
        it[USER_GENDER] = user.gender.name
        it[USER_WEEKLY_GOAL_IN_KM] = user.weeklyGoalInKM
        it[USER_WEIGHT_IN_KG] = user.weightInKg
    }
}