package com.digitex.designertools.service.qs

import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
internal fun TileService.updateIcon(@DrawableRes resId: Int) {
    qsTile.apply {
        icon = Icon.createWithResource(this@updateIcon, resId)
        updateTile()
    }
}