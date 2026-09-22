package com.sdevprem.runtrack.shared.data.utils

interface LocalFileProcessor {
    fun getFilePath(fileName: String): String?

    fun getFileNameFromURI(uri: String): String {
        return uri.substringAfterLast('/')
    }
}