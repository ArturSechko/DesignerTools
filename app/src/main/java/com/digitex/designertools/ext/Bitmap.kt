package com.digitex.designertools.ext

import android.graphics.Bitmap
import java.io.FileNotFoundException
import java.io.FileOutputStream

@Throws(FileNotFoundException::class)
fun Bitmap.save(path: String, quality: Int = 80): Boolean {
    return compress(Bitmap.CompressFormat.JPEG, quality, FileOutputStream(path))
}