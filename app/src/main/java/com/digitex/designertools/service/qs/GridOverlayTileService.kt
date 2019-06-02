package com.digitex.designertools.service.qs

import android.os.Build
import androidx.annotation.RequiresApi
import com.digitex.designertools.R
import com.digitex.designertools.designerApplication
import com.digitex.designertools.utils.cancelGridOverlay
import com.digitex.designertools.utils.launchGridOverlay

@RequiresApi(Build.VERSION_CODES.N)
class GridOverlayTileService : TileServiceBase() {

    override val tileIsOn: Boolean get() = designerApplication.isGridOverlayOn
    override val tileOnResId: Int get() = R.drawable.ic_qs_grid_on
    override val tileOffResId: Int get() = R.drawable.ic_qs_grid_off

    override fun onClick() {
        super.onClick()
        if (tileIsOn) {
            cancelGridOverlay()
        } else {
            launchGridOverlay()
        }
    }

}