package com.digitex.designertools.service.qs

import android.os.Build
import androidx.annotation.RequiresApi
import com.digitex.designertools.R
import com.digitex.designertools.designerApplication
import com.digitex.designertools.qs.OnOffTileState
import com.digitex.designertools.utils.cancelMockOverlayOrUnpublishTile
import com.digitex.designertools.utils.launchMockOverlayOrPublishTile

@RequiresApi(Build.VERSION_CODES.N)
class MockOverlayTileService : TileServiceBase() {

    override val tileIsOn: Boolean get() = designerApplication.isMockOverlayOn
    override val tileOnResId: Int get() = R.drawable.ic_qs_overlay_on
    override val tileOffResId: Int get() = R.drawable.ic_qs_overlay_off

    override fun onClick() {
        super.onClick()
        if (tileIsOn) {
            cancelMockOverlayOrUnpublishTile()
        } else {
            launchMockOverlayOrPublishTile(OnOffTileState.STATE_OFF)
        }
    }

}