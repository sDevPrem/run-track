package com.sdevprem.runtrack.shared.ui.screen.profile

import coil3.Uri

interface ProfileEditActions {
    fun startEditing()

    fun saveUser()

    fun updateUserName(newName: String)

    fun updateImgUri(newUri: Uri?)

    fun cancelEditing()
}