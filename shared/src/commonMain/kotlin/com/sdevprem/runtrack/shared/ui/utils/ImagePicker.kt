package com.sdevprem.runtrack.shared.ui.utils

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePicker(
    onImageSaved: (String?) -> Unit
): () -> Unit