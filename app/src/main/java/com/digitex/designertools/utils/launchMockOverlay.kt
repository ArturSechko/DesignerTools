package com.digitex.designertools.utils

import android.content.Intent
import android.os.Build
import com.digitex.designertools.designerApplication
import com.digitex.designertools.overlays.MockOverlay
import com.digitex.designertools.qs.MockQuickSettingsTile
import com.digitex.designertools.ui.StartOverlayActivity

fun launchMockOverlay() = startOverlayActivity(StartOverlayActivity.MOCK_OVERLAY)

fun publishMockOverlayTile(state: Int) = MockQuickSettingsTile.publishMockTile(state)

fun unpublishMockOverlayTile() = MockQuickSettingsTile.unpublishMockTile()

fun cancelMockOverlay() {
    val newIntent = Intent(designerApplication, MockOverlay::class.java)
    designerApplication.stopService(newIntent)
    Preferences.Mock.setMockOverlayActive(false)
    Preferences.Mock.setMockQsTileEnabled(false)
}

fun cancelMockOverlayOrUnpublishTile() {
    if (isCyanogenMod() && isAtLeastSdk(Build.VERSION_CODES.N)) {
        unpublishMockOverlayTile()
    } else {
        cancelMockOverlay()
    }
}

fun launchMockOverlayOrPublishTile(state: Int) {
    if (isCyanogenMod() && isAtLeastSdk(Build.VERSION_CODES.N)) {
        publishMockOverlayTile(state)
    } else {
        launchMockOverlay()
    }
}
