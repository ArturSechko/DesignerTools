package com.digitex.designertools.service.qs;

import android.annotation.TargetApi;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import com.digitex.designertools.DesignerToolsApplication;
import com.digitex.designertools.R;
import com.digitex.designertools.qs.OnOffTileState;
import com.digitex.designertools.utils.LaunchUtils;

@TargetApi(Build.VERSION_CODES.N)
public class ColorPickerTileService extends TileService {
    private static final String TAG = ColorPickerTileService.class.getSimpleName();

    public ColorPickerTileService() {
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
        updateTile(((DesignerToolsApplication) getApplication()).getColorPickerOn());
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        boolean isOn = ((DesignerToolsApplication) getApplicationContext()).getColorPickerOn();
        if (isOn) {
            LaunchUtils.publishColorPickerTile(this, OnOffTileState.STATE_OFF);
            LaunchUtils.cancelColorPickerOverlay(this);
        } else {
            LaunchUtils.publishColorPickerTile(this, OnOffTileState.STATE_ON);
            LaunchUtils.launchColorPickerOverlay(this);
        }
        updateTile(!isOn);
    }

    private void updateTile(boolean isOn) {
        final Tile tile = getQsTile();
        tile.setIcon(Icon.createWithResource(this, isOn
                ? R.drawable.ic_qs_colorpicker_on
                : R.drawable.ic_qs_colorpicker_off));
        tile.updateTile();
    }
}
