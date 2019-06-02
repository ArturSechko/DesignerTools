package com.digitex.designertools.utils

import android.content.Intent
import android.os.Build
import com.digitex.designertools.designerApplication
import com.digitex.designertools.ui.StartOverlayActivity

internal fun isAtLeastSdk(version: Int): Boolean = Build.VERSION.SDK_INT >= version

internal fun isCyanogenMod(): Boolean {
    return designerApplication.packageManager.hasSystemFeature("com.digitex.theme")
}

internal fun startOverlayActivity(overlayType: Int) {
    designerApplication.startActivity(Intent(designerApplication, StartOverlayActivity::class.java).also {
        it.putExtra(StartOverlayActivity.EXTRA_OVERLAY_TYPE, overlayType)
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}