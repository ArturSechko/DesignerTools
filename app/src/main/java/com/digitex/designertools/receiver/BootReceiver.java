package com.digitex.designertools.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.digitex.designertools.qs.ColorPickerQuickSettingsTile;
import com.digitex.designertools.qs.GridQuickSettingsTile;
import com.digitex.designertools.qs.MockQuickSettingsTile;
import com.digitex.designertools.service.ScreenshotListenerService;
import com.digitex.designertools.utils.LaunchUtils;
import com.digitex.designertools.utils.PreferenceUtils;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final boolean isCm = LaunchUtils.isCyanogenMod(context);
        if (PreferenceUtils.GridPreferences.getGridQsTileEnabled(context, false)) {
            PreferenceUtils.GridPreferences.setGridOverlayActive(context, false);
            if (isCm) GridQuickSettingsTile.publishGridTile(context);
        }
        if (PreferenceUtils.MockPreferences.getMockQsTileEnabled(context, false)) {
            PreferenceUtils.MockPreferences.setMockOverlayActive(context, false);
            if (isCm) MockQuickSettingsTile.publishMockTile(context);
        }
        if (PreferenceUtils.ColorPickerPreferences.getColorPickerQsTileEnabled(context, false)) {
            PreferenceUtils.ColorPickerPreferences.setColorPickerActive(context, false);
            if (isCm) ColorPickerQuickSettingsTile.publishColorPickerTile(context);
        }
        if (PreferenceUtils.ScreenshotPreferences.getScreenshotInfoEnabled(context, false)) {
            Intent newIntent = new Intent(context, ScreenshotListenerService.class);
            context.startService(newIntent);
        }
    }
}
