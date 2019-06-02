package com.digitex.designertools.utils

import android.app.Activity
import android.content.Intent
import android.os.Build
import com.digitex.designertools.designerApplication
import com.digitex.designertools.overlays.ColorPickerOverlay
import com.digitex.designertools.qs.ColorPickerQuickSettingsTile
import com.digitex.designertools.ui.ScreenRecordRequestActivity
import com.digitex.designertools.ui.StartOverlayActivity

fun launchColorPickerOverlay() = startOverlayActivity(StartOverlayActivity.COLOR_PICKER_OVERLAY)

fun startColorPickerOrRequestPermission() {
    if (designerApplication.screenRecordResultCode == Activity.RESULT_OK
            && designerApplication.screenRecordResultData != null) {
        designerApplication.startService(Intent(designerApplication, ColorPickerOverlay::class.java))
        Preferences.ColorPicker.setColorPickerActive(true)
        Preferences.ColorPicker.setColorPickerQsTileEnabled(true)
    } else {
        designerApplication.startActivity(
                Intent(designerApplication, ScreenRecordRequestActivity::class.java).also {
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
        )
    }
}

fun publishColorPickerTile(state: Int) {
    ColorPickerQuickSettingsTile.publishColorPickerTile(state)
}

fun unpublishColorPickerTile() {
    ColorPickerQuickSettingsTile.unpublishColorPickerTile()
}

fun cancelColorPickerOverlay() {
    val newIntent = Intent(designerApplication, ColorPickerOverlay::class.java)
    designerApplication.stopService(newIntent)
    Preferences.ColorPicker.setColorPickerActive(false)
    Preferences.ColorPicker.setColorPickerQsTileEnabled(false)
}

fun cancelColorPickerOrUnpublishTile() {
    if (isCyanogenMod() && isAtLeastSdk(Build.VERSION_CODES.N)) {
        unpublishColorPickerTile()
    } else {
        cancelColorPickerOverlay()
    }
}

fun launchColorPickerOrPublishTile(state: Int) {
    if (isCyanogenMod() && isAtLeastSdk(Build.VERSION_CODES.N)) {
        publishColorPickerTile(state)
    } else {
        launchColorPickerOverlay()
    }
}