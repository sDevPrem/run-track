package com.sdevprem.runtrack.shared.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sdevprem.runtrack.shared.data.model.Gender
import com.sdevprem.runtrack.shared.data.model.User
import com.sdevprem.runtrack.shared.data.utils.LocalFileProcessor
import kotlinx.coroutines.flow.map

class UserRepository(
    private val dataStore: DataStore<Preferences>,
    private val localFileProcessor: LocalFileProcessor
) {

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_GENDER = stringPreferencesKey("user_gender")
        val USER_WEIGHT_IN_KG = floatPreferencesKey("user_weight_in_kg")
        val USER_WEEKLY_GOAL_IN_KM = floatPreferencesKey("user_weekly_goal_in_km")
        val USER_IMG_URI = stringPreferencesKey("user_img_uri")
        val USER_IMG_FILE_NAME = stringPreferencesKey("user_img_file_name")
    }

    val user = dataStore.data.map {
        val dbImgUri = it[USER_IMG_URI]
        val userFileName = it[USER_IMG_FILE_NAME]
        User(
            name = it[USER_NAME] ?: "",
            gender = Gender.valueOf(it[USER_GENDER] ?: Gender.MALE.name),
            weightInKg = it[USER_WEIGHT_IN_KG] ?: 0.0f,
            weeklyGoalInKM = it[USER_WEEKLY_GOAL_IN_KM] ?: 0.0f,
            imgUri = if (userFileName.isNullOrBlank().not()) {
                localFileProcessor.getFilePath(userFileName)
            } else {
                if(dbImgUri.isNullOrBlank().not()) {
                    dbImgUri
                } else {
                    null
                }
            }
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
        user.imgUri?.let {
            uri -> it[USER_IMG_FILE_NAME] = localFileProcessor.getFileNameFromURI(uri)
        }
    }
}