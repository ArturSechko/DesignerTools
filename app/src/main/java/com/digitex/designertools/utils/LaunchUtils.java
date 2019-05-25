package com.digitex.designertools.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.digitex.designertools.DesignerToolsApplication;
import com.digitex.designertools.overlays.ColorPickerOverlay;
import com.digitex.designertools.overlays.GridOverlay;
import com.digitex.designertools.overlays.MockOverlay;
import com.digitex.designertools.qs.ColorPickerQuickSettingsTile;
import com.digitex.designertools.qs.GridQuickSettingsTile;
import com.digitex.designertools.qs.MockQuickSettingsTile;
import com.digitex.designertools.ui.ScreenRecordRequestActivity;
import com.digitex.designertools.ui.StartOverlayActivity;
import com.digitex.designertools.utils.PreferenceUtils.ColorPickerPreferences;
import com.digitex.designertools.utils.PreferenceUtils.GridPreferences;
import com.digitex.designertools.utils.PreferenceUtils.MockPreferences;

public class LaunchUtils {
    public static boolean isCyanogenMod(Context context) {
        return context.getPackageManager().hasSystemFeature("com.digitex.theme");
    }

    public static void publishGridOverlayTile(Context context, int state) {
        GridQuickSettingsTile.publishGridTile(context, state);
    }

    public static void launchGridOverlay(Context context) {
        startOverlayActivity(context, StartOverlayActivity.GRID_OVERLAY);
    }

    public static void lauchGridOverlayOrPublishTile(Context context, int state) {
        if (isCyanogenMod(context) && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            publishGridOverlayTile(context, state);
        } else {
            launchGridOverlay(context);
        }
    }

    public static void unpublishGridOverlayTile(Context context) {
        GridQuickSettingsTile.unpublishGridTile(context);
    }

    public static void cancelGridOverlay(Context context) {
        Intent newIntent = new Intent(context, GridOverlay.class);
        context.stopService(newIntent);
        GridPreferences.setGridOverlayActive(context, false);
        GridPreferences.setGridQsTileEnabled(context, false);
    }

    public static void cancelGridOverlayOrUnpublishTile(Context context) {
        if (isCyanogenMod(context) && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            unpublishGridOverlayTile(context);
            GridQuickSettingsTile.unpublishGridTile(context);
        } else {
            cancelGridOverlay(context);
        }
    }

    public static void publishMockOverlayTile(Context context, int state) {
        MockQuickSettingsTile.publishMockTile(context, state);
    }

    public static void launchMockOverlay(Context context) {
        startOverlayActivity(context, StartOverlayActivity.MOCK_OVERLAY);
    }

    public static void lauchMockPverlayOrPublishTile(Context context, int state) {
        if (isCyanogenMod(context) && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            publishMockOverlayTile(context, state);
        } else {
            launchMockOverlay(context);
        }
    }

    public static void unpublishMockOverlayTile(Context context) {
        MockQuickSettingsTile.unpublishMockTile(context);
    }

    public static void cancelMockOverlay(Context context) {
        Intent newIntent = new Intent(context, MockOverlay.class);
        context.stopService(newIntent);
        MockPreferences.setMockOverlayActive(context, false);
        MockPreferences.setMockQsTileEnabled(context, false);
    }

    public static void cancelMockOverlayOrUnpublishTile(Context context) {
        if (isCyanogenMod(context) && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            unpublishMockOverlayTile(context);
        } else {
            cancelMockOverlay(context);
        }
    }

    public static void publishColorPickerTile(Context context, int state) {
        ColorPickerQuickSettingsTile.publishColorPickerTile(context, state);
    }

    public static void launchColorPickerOverlay(Context context) {
        startOverlayActivity(context, StartOverlayActivity.COLOR_PICKER_OVERLAY);
    }

    public static void lauchColorPickerOrPublishTile(Context context, int state) {
        if (isCyanogenMod(context) && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            publishColorPickerTile(context, state);
        } else {
            launchColorPickerOverlay(context);
        }
    }

    public static void unpublishColorPickerTile(Context context) {
        ColorPickerQuickSettingsTile.unpublishColorPickerTile(context);
    }

    public static void cancelColorPickerOverlay(Context context) {
        Intent newIntent = new Intent(context, ColorPickerOverlay.class);
        context.stopService(newIntent);
        ColorPickerPreferences.setColorPickerActive(context, false);
        ColorPickerPreferences.setColorPickerQsTileEnabled(context, false);
    }

    public static void cancelColorPickerOrUnpublishTile(Context context) {
        if (isCyanogenMod(context) && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            unpublishColorPickerTile(context);
        } else {
            cancelColorPickerOverlay(context);
        }
    }

    public static void startColorPickerOrRequestPermission(Context context) {
        DesignerToolsApplication app =
                (DesignerToolsApplication) context.getApplicationContext();
        if (app.getScreenRecordResultCode() == Activity.RESULT_OK && app.getScreenRecordResultData() != null) {
            Intent newIntent = new Intent(context, ColorPickerOverlay.class);
            context.startService(newIntent);
            ColorPickerPreferences.setColorPickerActive(context, true);
            ColorPickerPreferences.setColorPickerQsTileEnabled(context, true);
        } else {
            Intent intent = new Intent(context, ScreenRecordRequestActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    private static void startOverlayActivity(Context context, int overlayType) {
        Intent intent = new Intent(context, StartOverlayActivity.class);
        intent.putExtra(StartOverlayActivity.EXTRA_OVERLAY_TYPE, overlayType);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
