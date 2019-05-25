package com.digitex.designertools.qs;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.digitex.designertools.R;
import com.digitex.designertools.utils.LaunchUtils;
import com.digitex.designertools.utils.PreferenceUtils.MockPreferences;

import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;

public class MockQuickSettingsTile {
    private static final String TAG = MockQuickSettingsTile.class.getSimpleName();

    public static final String ACTION_TOGGLE_STATE =
            "com.digitex.designertools.action.TOGGLE_MOCK_STATE";

    public static final String ACTION_UNPUBLISH =
            "com.digitex.designertools.action.UNPUBLISH_MOCK_TILE";

    public static final int TILE_ID = 2000;

    public static void publishMockTile(Context context) {
        publishMockTile(context, OnOffTileState.STATE_OFF);
    }

    public static void publishMockTile(Context context, int state) {
        Intent intent = new Intent(ACTION_TOGGLE_STATE);
        intent.putExtra(OnOffTileState.EXTRA_STATE, state);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        int iconResId = state == OnOffTileState.STATE_OFF ? R.drawable.ic_qs_overlay_off :
                R.drawable.ic_qs_overlay_on;
        CustomTile tile = new CustomTile.Builder(context)
                .setOnClickIntent(pi)
                .setLabel(context.getString(R.string.mock_qs_tile_label))
                .setIcon(iconResId)
                .build();
        CMStatusBarManager.getInstance(context).publishTile(TAG, TILE_ID, tile);
        MockPreferences.setMockQsTileEnabled(context, true);
    }

    public static void unpublishMockTile(Context context) {
        CMStatusBarManager.getInstance(context).removeTile(TAG, TILE_ID);
        MockPreferences.setMockQsTileEnabled(context, false);
        Intent intent = new Intent(MockQuickSettingsTile.ACTION_UNPUBLISH);
        context.sendBroadcast(intent);
    }

    public static class ClickBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(OnOffTileState.EXTRA_STATE, OnOffTileState.STATE_OFF);
            if (state == OnOffTileState.STATE_OFF) {
                LaunchUtils.publishMockOverlayTile(context, OnOffTileState.STATE_ON);
                LaunchUtils.launchMockOverlay(context);
            } else {
                LaunchUtils.publishMockOverlayTile(context, OnOffTileState.STATE_OFF);
                LaunchUtils.cancelMockOverlay(context);
            }
        }
    }
}
