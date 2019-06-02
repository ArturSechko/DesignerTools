package com.digitex.designertools.ext

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.digitex.designertools.designerApplication

fun Uri.getBitmap(): Bitmap? {
    var image: Bitmap? = null
    if (ContentResolver.SCHEME_CONTENT == scheme || ContentResolver.SCHEME_FILE == scheme) {
        designerApplication.contentResolver.openInputStream(this).use {
            try {
                image = BitmapFactory.decodeStream(it)
            } catch (e: Exception) {
                Log.w("Uri", "Unable to open content: $this", e)
            }
        }
    }

    return image
}