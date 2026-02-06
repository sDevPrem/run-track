package com.sdevprem.runtrack.shared.ui.utils

import androidx.compose.runtime.Composable

@Composable
actual fun rememberImagePicker(
    onImageSaved: (String?) -> Unit
): () -> Unit {
    return {}
}