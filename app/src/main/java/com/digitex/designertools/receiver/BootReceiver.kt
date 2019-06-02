package com.digitex.designertools.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.digitex.designertools.qs.ColorPickerQuickSettingsTile
import com.digitex.designertools.qs.GridQuickSettingsTile
import com.digitex.designertools.qs.MockQuickSettingsTile
import com.digitex.designertools.service.ScreenshotListenerService
import com.digitex.designertools.utils.Preferences
import com.digitex.designertools.utils.isCyanogenMod

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val isCm = isCyanogenMod()
            if (Preferences.Grid.getGridQsTileEnabled()) {
                Preferences.Grid.setGridOverlayActive(false)
                if (isCm) GridQuickSettingsTile.publishGridTile()
            }
            if (Preferences.Mock.getMockQsTileEnabled()) {
                Preferences.Mock.setMockOverlayActive(false)
                if (isCm) MockQuickSettingsTile.publishMockTile()
            }
            if (Preferences.ColorPicker.getColorPickerQsTileEnabled()) {
                Preferences.ColorPicker.setColorPickerActive(false)
                if (isCm) ColorPickerQuickSettingsTile.publishColorPickerTile()
            }
            if (Preferences.Screenshot.getScreenshotInfoEnabled()) {
                context.startService(Intent(context, ScreenshotListenerService::class.java))
            }
        }
    }
}
