package com.digitex.designertools.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.digitex.designertools.designerApplication
import com.digitex.designertools.utils.*

class AppShortcutsActivity : AppCompatActivity() {

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
        if (designerApplication.isGridOverlayOn) {
            cancelGridOverlay()
        } else {
            launchGridOverlay()
        }
    }

    private fun toggleMockOverlay() {
        if (designerApplication.isMockOverlayOn) {
            cancelMockOverlay()
        } else {
            launchMockOverlay()
        }
    }

    private fun toggleColorPickerOverlay() {
        if (designerApplication.isColorPickerOn) {
            cancelColorPickerOverlay()
        } else {
            launchColorPickerOverlay()
        }
    }

    companion object {
        private const val ACTION_SHOW_GRID_OVERLAY = "com.digitex.designertools.action.SHOW_GRID_OVERLAY"
        private const val ACTION_SHOW_MOCK_OVERLAY = "com.digitex.designertools.action.SHOW_MOCK_OVERLAY"
        private const val ACTION_SHOW_COLOR_PICKER_OVERLAY = "com.digitex.designertools.action.SHOW_COLOR_PICKER_OVERLAY"
    }
}
