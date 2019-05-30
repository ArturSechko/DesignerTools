package com.digitex.designertools.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity

import com.digitex.designertools.overlays.GridOverlay
import com.digitex.designertools.overlays.MockOverlay
import com.digitex.designertools.utils.LaunchUtils
import com.digitex.designertools.utils.PreferenceUtils.GridPreferences
import com.digitex.designertools.utils.PreferenceUtils.MockPreferences

class StartOverlayActivity : AppCompatActivity() {

    private var overlayType = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra(EXTRA_OVERLAY_TYPE)) {
            overlayType = intent.getIntExtra(EXTRA_OVERLAY_TYPE, -1)
            if (Settings.canDrawOverlays(this)) {
                startOverlayService(overlayType)
                finish()
            } else {
                val closeDialogsIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                sendBroadcast(closeDialogsIntent)
                val newIntent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                )
                startActivityForResult(newIntent, REQUEST_OVERLAY_PERMISSION)
            }
        } else {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                startOverlayService(overlayType)
            }
            finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun startOverlayService(overlayType: Int) {
        var newIntent = Intent(this, GridOverlay::class.java)
        when (overlayType) {
            GRID_OVERLAY -> {
                startService(newIntent)
                GridPreferences.setGridOverlayActive(this, true)
                GridPreferences.setGridQsTileEnabled(this, true)
            }
            MOCK_OVERLAY -> {
                newIntent = Intent(this, MockOverlay::class.java)
                startService(newIntent)
                MockPreferences.setMockOverlayActive(this, true)
                MockPreferences.setMockQsTileEnabled(this, true)
            }
            COLOR_PICKER_OVERLAY -> LaunchUtils.startColorPickerOrRequestPermission(this)
        }
    }

    companion object {
        private const val REQUEST_OVERLAY_PERMISSION = 42
        const val EXTRA_OVERLAY_TYPE = "overlayType"
        const val GRID_OVERLAY = 0
        const val MOCK_OVERLAY = 1
        const val COLOR_PICKER_OVERLAY = 2
    }
}
