package com.digitex.designertools.service.qs

import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
abstract class TileServiceBase : TileService() {

    abstract val tileIsOn: Boolean
    abstract val tileOnResId: Int
    abstract val tileOffResId: Int

    override fun onStartListening() {
        super.onStartListening()
        updateTile(tileIsOn)
    }

    override fun onClick() {
        updateTile(!tileIsOn)
    }

    private fun updateTile(isOn: Boolean) {
        updateIcon(if (isOn) tileOnResId else tileOffResId)
    }

}