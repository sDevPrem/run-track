package com.sdevprem.runtrack.shared.data.utils

import android.content.Context
import java.io.File

class AndroidLocalFileProcessor(
    private val context: Context
): LocalFileProcessor {
    override fun getFilePath(fileName: String): String? {
        return File(context.filesDir, fileName).path
    }
}