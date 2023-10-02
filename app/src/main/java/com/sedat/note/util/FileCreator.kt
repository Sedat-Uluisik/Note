package com.sedat.note.util

import androidx.camera.core.ImageCapture

object FileCreator {
    const val JPEG_FORMAT = ".jpg"
    fun createTempFile(fileFormat: String) = ImageCapture.OutputFileOptions.Builder(createTempFile(System.currentTimeMillis().toString(), fileFormat)).build()
}