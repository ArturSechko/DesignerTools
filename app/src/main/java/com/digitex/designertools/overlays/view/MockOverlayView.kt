package com.digitex.designertools.overlays.view

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.widget.ImageView
import com.digitex.designertools.utils.MockupUtils
import com.digitex.designertools.utils.PreferenceUtils

internal class MockOverlayView(context: Context) : ImageView(context) {

    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (PreferenceUtils.MockPreferences.KEY_MOCKUP_OVERLAY_PORTRAIT == key || PreferenceUtils.MockPreferences.KEY_MOCKUP_OVERLAY_LANDSCAPE == key) {
            setImageBitmap(getBitmapForOrientation(resources.configuration.orientation))
            invalidate()
        } else if (PreferenceUtils.MockPreferences.KEY_MOCK_OPACITY == key) {
            imageAlpha = PreferenceUtils.MockPreferences.getMockOpacity(getContext(), 10)
            invalidate()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val prefs = PreferenceUtils.getShardedPreferences(context)
        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
        setImageBitmap(getBitmapForOrientation(resources.configuration.orientation))
        imageAlpha = PreferenceUtils.MockPreferences.getMockOpacity(context, 10)
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        val prefs = PreferenceUtils.getShardedPreferences(context)
        prefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setImageBitmap(getBitmapForOrientation(resources.configuration.orientation))
    }

    private fun getBitmapForOrientation(orientation: Int): Bitmap? {
        return if (orientation == Configuration.ORIENTATION_PORTRAIT)
            MockupUtils.getPortraitMockup(context)
        else
            MockupUtils.getLandscapeMockup(context)
    }
}