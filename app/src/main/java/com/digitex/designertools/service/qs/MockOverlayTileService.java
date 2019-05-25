package com.digitex.designertools.service.qs;

import android.annotation.TargetApi;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import com.digitex.designertools.DesignerToolsApplication;
import com.digitex.designertools.R;
import com.digitex.designertools.utils.LaunchUtils;

@TargetApi(Build.VERSION_CODES.N)
public class MockOverlayTileService extends TileService {
    private static final String TAG = MockOverlayTileService.class.getSimpleName();

    public MockOverlayTileService() {
        super();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile(((DesignerToolsApplication) getApplicationContext()).getMockOverlayOn());
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        boolean isOn = ((DesignerToolsApplication) getApplicationContext()).getMockOverlayOn();
        if (isOn) {
            LaunchUtils.cancelMockOverlayOrUnpublishTile(this);
        } else {
            LaunchUtils.lauchMockPverlayOrPublishTile(this, 0);
        }
        updateTile(!isOn);
    }

    private void updateTile(boolean isOn) {
        final Tile tile = getQsTile();
        tile.setIcon(Icon.createWithResource(this, isOn
                ? R.drawable.ic_qs_overlay_on
                : R.drawable.ic_qs_overlay_off));
        tile.updateTile();
    }
}
