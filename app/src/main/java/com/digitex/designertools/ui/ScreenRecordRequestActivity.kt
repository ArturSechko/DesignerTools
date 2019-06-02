package com.digitex.designertools.ui

import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService

import com.digitex.designertools.designerApplication
import com.digitex.designertools.overlays.ColorPickerOverlay
import com.digitex.designertools.utils.PreferenceUtils.ColorPickerPreferences

class ScreenRecordRequestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mpm = getSystemService<MediaProjectionManager>()!!
        startActivityForResult(mpm.createScreenCaptureIntent(), 42)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            designerApplication.setScreenRecordPermissionData(resultCode, data)
            startService(Intent(this, ColorPickerOverlay::class.java))
            ColorPickerPreferences.setColorPickerActive(this, true)
        }
        super.onActivityResult(requestCode, resultCode, data)
        finish()
    }
}
