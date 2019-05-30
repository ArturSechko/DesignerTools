package com.digitex.designertools.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.digitex.designertools.DesignerToolsApplication
import com.digitex.designertools.utils.LaunchUtils

class AppShortcutsActivity : AppCompatActivity() {

    private val designerToolsApplication: DesignerToolsApplication
        get() = application as DesignerToolsApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when {
            ACTION_SHOW_GRID_OVERLAY == intent.action -> toggleGridOverlay()
            ACTION_SHOW_MOCK_OVERLAY == intent.action -> toggleMockOverlay()
            ACTION_SHOW_COLOR_PICKER_OVERLAY == intent.action -> toggleColorPickerOverlay()
        }
        finish()
    }

    private fun toggleGridOverlay() {
        if (designerToolsApplication.gridOverlayOn) {
            LaunchUtils.cancelGridOverlay(this)
        } else {
            LaunchUtils.launchGridOverlay(this)
        }
    }

    private fun toggleMockOverlay() {
        if (designerToolsApplication.mockOverlayOn) {
            LaunchUtils.cancelMockOverlay(this)
        } else {
            LaunchUtils.launchMockOverlay(this)
        }
    }

    private fun toggleColorPickerOverlay() {
        if (designerToolsApplication.colorPickerOn) {
            LaunchUtils.cancelColorPickerOverlay(this)
        } else {
            LaunchUtils.launchColorPickerOverlay(this)
        }
    }

    companion object {
        private const val ACTION_SHOW_GRID_OVERLAY = "com.digitex.designertools.action.SHOW_GRID_OVERLAY"
        private const val ACTION_SHOW_MOCK_OVERLAY = "com.digitex.designertools.action.SHOW_MOCK_OVERLAY"
        private const val ACTION_SHOW_COLOR_PICKER_OVERLAY = "com.digitex.designertools.action.SHOW_COLOR_PICKER_OVERLAY"
    }
}
