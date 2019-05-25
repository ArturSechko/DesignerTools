package com.digitex.designertools.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.digitex.designertools.R;
import com.digitex.designertools.qs.OnOffTileState;
import com.digitex.designertools.utils.LaunchUtils;
import com.digitex.designertools.utils.PreferenceUtils.ColorPickerPreferences;

public class ColorPickerCardFragment extends DesignerToolCardFragment {
    private static final int REQUEST_OVERLAY_PERMISSION = 0x42;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View base = super.onCreateView(inflater, container, savedInstanceState);
        setTitleText(R.string.header_title_color_picker);
        setTitleSummary(R.string.header_summary_color_picker);
        setIconResource(R.drawable.ic_qs_colorpicker_on);
        base.setBackgroundTintList(ColorStateList.valueOf(
                getResources().getColor(R.color.colorColorPickerCardTint)));

        return base;
    }

    @Override
    public void onResume() {
        super.onResume();
        mEnabledSwitch.setChecked(getApplicationContext().getColorPickerOn());
    }

    @Override
    protected int getCardStyleResourceId() {
        return R.style.AppTheme_ColorPickerCard;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked == getApplicationContext().getColorPickerOn()) return;
        if (isChecked) {
            enableFeature(true);
        } else {
            enableFeature(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(getContext())) {
                mEnabledSwitch.setChecked(true);
            } else {
                mEnabledSwitch.setChecked(false);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void isEnabled() {
    }

    private void enableFeature(boolean enable) {
        if (enable) {
            LaunchUtils.lauchColorPickerOrPublishTile(getContext(),
                    ColorPickerPreferences.getColorPickerActive(getContext(), false)
                            ? OnOffTileState.STATE_ON
                            : OnOffTileState.STATE_OFF);
        } else {
            LaunchUtils.cancelColorPickerOrUnpublishTile(getContext());
        }
    }
}
