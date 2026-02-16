package com.sdevprem.runtrack.shared.data.utils

import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

class IosLocalFileProcessor: LocalFileProcessor {
    override fun getFilePath(fileName: String): String? {
        val fileManager = NSFileManager.defaultManager

        val urls = fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
        val documentLink = urls.first() as? NSURL ?: return null

        return documentLink.URLByAppendingPathComponent(fileName)?.path
    }
}