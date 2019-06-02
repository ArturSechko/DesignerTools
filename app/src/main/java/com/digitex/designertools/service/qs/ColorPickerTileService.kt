package com.digitex.designertools.service.qs

import android.os.Build
import androidx.annotation.RequiresApi
import com.digitex.designertools.R
import com.digitex.designertools.designerApplication
import com.digitex.designertools.qs.OnOffTileState
import com.digitex.designertools.utils.LaunchUtils

@RequiresApi(Build.VERSION_CODES.N)
class ColorPickerTileService : TileServiceBase() {

    override val tileIsOn: Boolean get() = designerApplication.isColorPickerOn
    override val tileOnResId: Int get() = R.drawable.ic_qs_colorpicker_on
    override val tileOffResId: Int get() = R.drawable.ic_qs_colorpicker_off

    override fun onClick() {
        super.onClick()
        if (tileIsOn) {
            LaunchUtils.publishColorPickerTile(this, OnOffTileState.STATE_OFF)
            LaunchUtils.cancelColorPickerOverlay(this)
        } else {
            LaunchUtils.publishColorPickerTile(this, OnOffTileState.STATE_ON)
            LaunchUtils.launchColorPickerOverlay(this)
        }
    }

}