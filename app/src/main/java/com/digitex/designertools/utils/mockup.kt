package com.digitex.designertools.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.digitex.designertools.designerApplication
import com.digitex.designertools.ext.save
import java.io.File
import java.io.IOException

private const val MOCKUP_DIRECTORY = "mockups"
private const val PORTRAIT_MOCKUP_FILENAME = "mockup_portrait"
private const val LANDSCAPE_MOCKUP_FILENAME = "mockup_landscape"

@Throws(IOException::class)
fun savePortraitMockup(bitmap: Bitmap?) {
    save(bitmap, PORTRAIT_MOCKUP_FILENAME)
}

fun getPortraitMockup(): Bitmap? {
    return load(PORTRAIT_MOCKUP_FILENAME)
}

@Throws(IOException::class)
fun saveLandscapeMockup(bitmap: Bitmap?) {
    save(bitmap, LANDSCAPE_MOCKUP_FILENAME)
}

fun getLandscapeMockup(): Bitmap? {
    return load(LANDSCAPE_MOCKUP_FILENAME)
}

@Throws(IOException::class)
private fun save(bitmap: Bitmap?, fileName: String) {
    if (bitmap != null) {
        val path = designerApplication.filesDir.absolutePath + File.separator + MOCKUP_DIRECTORY
        val dir = File(path)
        if (!dir.exists() && !dir.mkdirs()) {
            throw IOException("Unable to mkdris")
        }
        val filePath = path + File.separator + fileName
        bitmap.save(path + File.separator + fileName)
        if (PORTRAIT_MOCKUP_FILENAME == fileName) {
            Preferences.Mock.setPortraitMockOverlay(filePath)
        } else if (LANDSCAPE_MOCKUP_FILENAME == fileName) {
            Preferences.Mock.setLandscapeMockOverlay(filePath)
        }
    }
}

private fun load(fileName: String): Bitmap? {
    val file = File("${designerApplication.filesDir.absolutePath}${File.separator}$MOCKUP_DIRECTORY${File.separator}$fileName")
    return if (!file.exists()) null else BitmapFactory.decodeFile(file.absolutePath)
}