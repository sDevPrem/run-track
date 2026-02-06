package com.sdevprem.runtrack.shared.ui.utils

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
actual fun rememberImagePicker(
    onImageSaved: (String?) -> Unit
): () -> Unit {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        coroutineScope.launch(Dispatchers.IO) {
            uri ?: return@launch
            val path = saveImage(uri, context)
            withContext(Dispatchers.Main) {
                onImageSaved(path)
            }
        }
    }

    return {
        launcher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }
}

private fun saveImage(uri: Uri, context: Context): String? {
    val contentResolver = context.contentResolver ?: return null
    contentResolver.openInputStream(uri)?.use { inputStream ->
        val localDir = context.filesDir
        val file = File(localDir, "photo${System.currentTimeMillis()}.jpg")

        file.outputStream().use {
            it.write(inputStream.readBytes())
        }
        return file.path
    }
    return null
}