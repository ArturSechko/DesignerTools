package com.digitex.designertools.utils

import android.content.Intent
import android.os.Build
import com.digitex.designertools.designerApplication
import com.digitex.designertools.overlays.GridOverlay
import com.digitex.designertools.qs.GridQuickSettingsTile
import com.digitex.designertools.ui.StartOverlayActivity

fun launchGridOverlay() = startOverlayActivity(StartOverlayActivity.GRID_OVERLAY)

fun publishGridOverlayTile(state: Int) = GridQuickSettingsTile.publishGridTile(state)

fun unpublishGridOverlayTile() = GridQuickSettingsTile.unpublishGridTile()

fun cancelGridOverlay() {
    val newIntent = Intent(designerApplication, GridOverlay::class.java)
    designerApplication.stopService(newIntent)
    Preferences.Grid.setGridOverlayActive(false)
    Preferences.Grid.setGridQsTileEnabled(false)
}

fun cancelGridOverlayOrUnpublishTile() {
    if (isCyanogenMod() && isAtLeastSdk(Build.VERSION_CODES.N)) {
        unpublishGridOverlayTile()
        GridQuickSettingsTile.unpublishGridTile()
    } else {
        cancelGridOverlay()
    }
}

fun lauchGridOverlayOrPublishTile(state: Int) {
    if (isCyanogenMod() && isAtLeastSdk(Build.VERSION_CODES.N)) {
        publishGridOverlayTile(state)
    } else {
        launchGridOverlay()
    }
}
