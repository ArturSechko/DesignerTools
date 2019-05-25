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
public class GridOverlayTileService extends TileService {
    private static final String TAG = GridOverlayTileService.class.getSimpleName();

    public GridOverlayTileService() {
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
        boolean isOn = ((DesignerToolsApplication) getApplicationContext()).getGridOverlayOn();
        updateTile(isOn);
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        boolean isOn = ((DesignerToolsApplication) getApplicationContext()).getGridOverlayOn();
        if (isOn) {
            LaunchUtils.cancelGridOverlay(this);
        } else {
            LaunchUtils.launchGridOverlay(this);
        }
        updateTile(!isOn);
    }

    private void updateTile(boolean isOn) {
        final Tile tile = getQsTile();
        tile.setIcon(Icon.createWithResource(this, isOn
                ? R.drawable.ic_qs_grid_on
                : R.drawable.ic_qs_grid_off));
        tile.updateTile();
    }
}
