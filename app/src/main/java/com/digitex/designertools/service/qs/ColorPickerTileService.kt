package com.digitex.designertools.service.qs

import android.os.Build
import androidx.annotation.RequiresApi
import com.digitex.designertools.R
import com.digitex.designertools.designerApplication
import com.digitex.designertools.qs.OnOffTileState
import com.digitex.designertools.utils.cancelColorPickerOverlay
import com.digitex.designertools.utils.launchColorPickerOverlay
import com.digitex.designertools.utils.publishColorPickerTile

@RequiresApi(Build.VERSION_CODES.N)
class ColorPickerTileService : TileServiceBase() {

    override val tileIsOn: Boolean get() = designerApplication.isColorPickerOn
    override val tileOnResId: Int get() = R.drawable.ic_qs_colorpicker_on
    override val tileOffResId: Int get() = R.drawable.ic_qs_colorpicker_off

    override fun onClick() {
        super.onClick()
        if (tileIsOn) {
            publishColorPickerTile(OnOffTileState.STATE_OFF)
            cancelColorPickerOverlay()
        } else {
            publishColorPickerTile(OnOffTileState.STATE_ON)
            launchColorPickerOverlay()
        }
    }

}