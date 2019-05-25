package com.digitex.designertools.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.digitex.designertools.R;

import com.digitex.designertools.qs.OnOffTileState;
import com.digitex.designertools.utils.ImageUtils;
import com.digitex.designertools.utils.LaunchUtils;
import com.digitex.designertools.utils.MockupUtils;
import com.digitex.designertools.utils.PreferenceUtils.MockPreferences;

import java.io.IOException;

public class MockupOverlayCardFragmnt extends DesignerToolCardFragment {
    private static final int REQUEST_PICK_PORTRAIT_IMAGE = 1000;
    private static final int REQUEST_PICK_LANDSCAPE_IMAGE = 1001;

    ImageView mPortraitImage;
    ImageView mLandscapeImage;
    Button mReset;
    TextView mOpacityText;
    SeekBar mOpacityLevel;

    @Override
    public void onResume() {
        super.onResume();
        mEnabledSwitch.setChecked(getApplicationContext().getMockOverlayOn());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View base = super.onCreateView(inflater, container, savedInstanceState);
        setTitleText(R.string.header_title_mockup_overlay);
        setTitleSummary(R.string.header_summary_mockup_overlay);
        setIconResource(R.drawable.ic_qs_overlay_on);
        base.setBackgroundTintList(ColorStateList.valueOf(
                getResources().getColor(R.color.colorMockupOverlayCardTint)));

        View v =inflater.inflate(R.layout.mockup_overlay_content, mCardContent);
        mPortraitImage = (ImageView) v.findViewById(R.id.portrait_image);
        mPortraitImage.setImageBitmap(MockupUtils.getPortraitMockup(getContext()));
        mPortraitImage.setOnClickListener(mImageClickListener);
        mLandscapeImage = (ImageView) v.findViewById(R.id.landscape_image);
        mLandscapeImage.setImageBitmap(MockupUtils.getLandscapeMockup(getContext()));
        mLandscapeImage.setOnClickListener(mImageClickListener);
        mReset = (Button) v.findViewById(R.id.reset);
        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MockupUtils.savePortraitMockup(getContext(), null);
                    mPortraitImage.setImageBitmap(null);
                    MockupUtils.saveLandscapeMockup(getContext(), null);
                    mLandscapeImage.setImageBitmap(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mOpacityText = (TextView) v.findViewById(R.id.opacity_text);
        mOpacityLevel = (SeekBar) v.findViewById(R.id.opacity);
        mOpacityLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int opacity = (progress + 1) *10;
                MockPreferences.setMockOpacity(getContext(), opacity);
                setOpacityLevel(opacity);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        int opacity = MockPreferences.getMockOpacity(getContext(), 10);
        setOpacityLevel(opacity);

        return base;
    }

    @Override
    protected int getCardStyleResourceId() {
        return R.style.AppTheme_MockupOverlayCard;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked == getApplicationContext().getMockOverlayOn()) return;
        if (isChecked) {
            LaunchUtils.lauchMockPverlayOrPublishTile(getContext(),
                    MockPreferences.getMockOverlayActive(getContext(), false)
                            ? OnOffTileState.STATE_ON
                            : OnOffTileState.STATE_OFF);
        } else {
            LaunchUtils.cancelMockOverlayOrUnpublishTile(getContext());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_PORTRAIT_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        Bitmap overlay = ImageUtils.getBitmapFromUri(getContext(), selectedImage);
                        try {
                            MockupUtils.savePortraitMockup(getContext(), overlay);
                            mPortraitImage.setImageBitmap(overlay);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case REQUEST_PICK_LANDSCAPE_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        Bitmap overlay = ImageUtils.getBitmapFromUri(getContext(), selectedImage);
                        try {
                            MockupUtils.saveLandscapeMockup(getContext(), overlay);
                            mLandscapeImage.setImageBitmap(overlay);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private View.OnClickListener mImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mPortraitImage || v == mLandscapeImage) {
                Intent intent = getPickImageIntent();
                startActivityForResult(intent, v == mPortraitImage ? REQUEST_PICK_PORTRAIT_IMAGE
                        : REQUEST_PICK_LANDSCAPE_IMAGE);
            }
        }
    };

    private void setOpacityLevel(int opacity) {
        mOpacityText.setText(getContext().getString(R.string.opacity_format, opacity));
        mOpacityLevel.setProgress(opacity / 10 - 1);
    }

    private Intent getPickImageIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        return intent;
    }
}
