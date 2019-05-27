package com.digitex.designertools.service.qs

import android.os.Build
import androidx.annotation.RequiresApi
import com.digitex.designertools.DesignerToolsApplication
import com.digitex.designertools.R
import com.digitex.designertools.utils.LaunchUtils

@RequiresApi(Build.VERSION_CODES.N)
class GridOverlayTileService : TileServiceBase() {

    override val tileIsOn: Boolean get() = (applicationContext as DesignerToolsApplication).gridOverlayOn
    override val tileOnResId: Int get() = R.drawable.ic_qs_grid_on
    override val tileOffResId: Int get() = R.drawable.ic_qs_grid_off

    override fun onClick() {
        super.onClick()
        if (tileIsOn) {
            LaunchUtils.cancelGridOverlay(this)
        } else {
            LaunchUtils.launchGridOverlay(this)
        }
    }

}