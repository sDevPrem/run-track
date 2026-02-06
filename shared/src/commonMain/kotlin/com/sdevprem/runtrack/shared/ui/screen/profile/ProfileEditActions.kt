package com.sdevprem.runtrack.shared.ui.screen.profile

interface ProfileEditActions {
    fun startEditing()

    fun saveUser()

    fun updateUserName(newName: String)

    fun updateImgUri(newUri: String?)

    fun cancelEditing()
}