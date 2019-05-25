package com.digitex.designertools.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.digitex.designertools.R;
import com.digitex.designertools.service.ScreenshotListenerService;
import com.digitex.designertools.utils.PreferenceUtils.ScreenshotPreferences;

public class ScreenshotCardFragment extends DesignerToolCardFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View base = super.onCreateView(inflater, container, savedInstanceState);
        setTitleText(R.string.header_title_screenshot);
        setTitleSummary(R.string.header_summary_screenshot);
        setIconResource(R.drawable.ic_qs_screenshotinfo_on);
        base.setBackgroundTintList(ColorStateList.valueOf(
                getResources().getColor(R.color.colorScreenshotCardTint)));

        mEnabledSwitch.setChecked(ScreenshotPreferences.getScreenshotInfoEnabled(getContext(),
                false));

        return base;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
            int[] grantResults) {
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ScreenshotPreferences.setScreenshotInfoEnabled(getContext(), true);
            Intent newIntent = new Intent(getContext(), ScreenshotListenerService.class);
            getContext().startService(newIntent);
            mEnabledSwitch.setChecked(true);
        } else {
            mEnabledSwitch.setChecked(false);
        }
    }

    @Override
    protected int getCardStyleResourceId() {
        return R.style.AppTheme_ScreenshotCard;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isVisible()) return;

        if (isChecked) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                ScreenshotPreferences.setScreenshotInfoEnabled(getContext(), isChecked);
                Intent newIntent = new Intent(getContext(), ScreenshotListenerService.class);
                getContext().startService(newIntent);
            } else {
                mEnabledSwitch.setChecked(false);
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 42);
            }
        } else {
            ScreenshotPreferences.setScreenshotInfoEnabled(getContext(), false);
        }
    }
}
