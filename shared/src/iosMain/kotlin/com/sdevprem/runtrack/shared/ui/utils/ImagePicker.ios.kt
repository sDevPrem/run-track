package com.sdevprem.runtrack.shared.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.uikit.LocalUIViewController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.Foundation.NSDate
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.writeToURL
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UniformTypeIdentifiers.UTTypeImage
import platform.darwin.NSObject

@Composable
actual fun rememberImagePicker(
    onImageSaved: (String?) -> Unit
): () -> Unit {
    val coroutineScope = rememberCoroutineScope()
    val pickerDelegate = remember { ImagePickerDelegate(onImageSaved, coroutineScope) }
    val uiViewController = LocalUIViewController.current
    return remember {
        {
            val controller = PHPickerViewController(
                configuration = PHPickerConfiguration().apply {
                    selectionLimit = 1
                    filter = PHPickerFilter.imagesFilter
                }
            ).apply {
                this.delegate = pickerDelegate
            }
            uiViewController.presentViewController(controller, animated = true, completion = null)
        }
    }
}

private class ImagePickerDelegate(
    private val onImageSaved: (String?) -> Unit,
    private val coroutineScope: CoroutineScope
) : NSObject(), PHPickerViewControllerDelegateProtocol {
    override fun picker(
        picker: PHPickerViewController,
        didFinishPicking: List<*>
    ) {
        picker.dismissViewControllerAnimated(true,completion = null)
        println("didFinishPicking: $didFinishPicking")

        val result = didFinishPicking.firstOrNull() as? PHPickerResult ?: return
        val itemProvider = result.itemProvider


        if (itemProvider.hasItemConformingToTypeIdentifier(UTTypeImage.identifier)) {
            itemProvider.loadDataRepresentationForTypeIdentifier(UTTypeImage.identifier) { data, err ->
                if (err != null || data == null) {
                    println("error on loading image err = $err data = $data")
                    return@loadDataRepresentationForTypeIdentifier
                }
                coroutineScope.launch(Dispatchers.IO) {
                    val image = UIImage(data = data)
                    val filePath = saveImage(image)
                    withContext(Dispatchers.Main) {
                        onImageSaved(filePath)
                    }
                }
            }
        }
    }

    private fun saveImage(uiImage: UIImage): String? {
        val jpegData = UIImageJPEGRepresentation(uiImage, 0.8) ?: return null

        val fileManager = NSFileManager.defaultManager
        val urls = fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
        val documentLink = urls.first() as? NSURL ?: return null

        val fileName = "photo${NSDate().timeIntervalSince1970}.jpg"
        val fileURL = documentLink.URLByAppendingPathComponent(fileName) ?: return null

        return if(jpegData.writeToURL(fileURL, true)) {
            fileURL.path ?: ""
        } else {
            println("error on saving")
            null
        }
    }

}