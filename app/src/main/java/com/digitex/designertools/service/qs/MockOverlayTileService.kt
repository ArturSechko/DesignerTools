package com.digitex.designertools.service.qs

import android.os.Build
import androidx.annotation.RequiresApi
import com.digitex.designertools.R
import com.digitex.designertools.designerApplication
import com.digitex.designertools.utils.LaunchUtils

@RequiresApi(Build.VERSION_CODES.N)
class MockOverlayTileService : TileServiceBase() {

    override val tileIsOn: Boolean get() = designerApplication.isMockOverlayOn
    override val tileOnResId: Int get() = R.drawable.ic_qs_overlay_on
    override val tileOffResId: Int get() = R.drawable.ic_qs_overlay_off

    override fun onClick() {
        super.onClick()
        if (tileIsOn) {
            LaunchUtils.cancelMockOverlayOrUnpublishTile(this)
        } else {
            LaunchUtils.lauchMockPverlayOrPublishTile(this, 0)
        }
    }

}