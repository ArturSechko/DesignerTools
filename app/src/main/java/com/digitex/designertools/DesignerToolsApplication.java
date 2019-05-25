package com.digitex.designertools;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.digitex.designertools.utils.PreferenceUtils.ColorPickerPreferences;
import com.digitex.designertools.utils.PreferenceUtils.GridPreferences;
import com.digitex.designertools.utils.PreferenceUtils.MockPreferences;

public class DesignerToolsApplication extends Application {

    private int mResultCode = Activity.RESULT_CANCELED;
    private Intent mResultData;

    private boolean mGridOverlayOn;
    private boolean mMockOverlayOn;
    private boolean mColorPickerOn;

    public void setScreenRecordPermissionData(int resultCode, Intent resultData) {
        mResultCode = resultCode;
        mResultData = resultData;
    }

    public int getScreenRecordResultCode() {
        return mResultCode;
    }

    public Intent getScreenRecordResultData() {
        return mResultData;
    }

    public void setGridOverlayOn(boolean on) {
        mGridOverlayOn = on;
    }

    public boolean getGridOverlayOn() {
        return mGridOverlayOn || GridPreferences.getGridQsTileEnabled(this, false);
    }

    public void setMockOverlayOn(boolean on) {
        mMockOverlayOn = on;
    }

    public boolean getMockOverlayOn() {
        return mMockOverlayOn || MockPreferences.getMockQsTileEnabled(this, false);
    }

    public void setColorPickerOn(boolean on) {
        mColorPickerOn = on;
    }

    public boolean getColorPickerOn() {
        return mColorPickerOn || ColorPickerPreferences.getColorPickerQsTileEnabled(this, false);
    }
}
