package com.digitex.designertools.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import com.digitex.designertools.DesignerToolsApplication;
import com.digitex.designertools.overlays.ColorPickerOverlay;
import com.digitex.designertools.utils.PreferenceUtils.ColorPickerPreferences;

public class ScreenRecordRequestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MediaProjectionManager mpm = getSystemService(MediaProjectionManager.class);
        startActivityForResult(mpm.createScreenCaptureIntent(), 42);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            ((DesignerToolsApplication) getApplication()).setScreenRecordPermissionData(
                    resultCode, data);
            Intent newIntent = new Intent(this, ColorPickerOverlay.class);
            this.startService(newIntent);
            ColorPickerPreferences.setColorPickerActive(this, true);
        }
        finish();
    }
}
