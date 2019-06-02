package com.digitex.designertools.overlays.view

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.widget.ImageView
import com.digitex.designertools.utils.Preferences
import com.digitex.designertools.utils.getLandscapeMockup
import com.digitex.designertools.utils.getPortraitMockup

internal class MockOverlayView(context: Context) : ImageView(context) {

    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (Preferences.Mock.mockOverlayPortrait == key || Preferences.Mock.mockOverlayLandscape == key) {
            setImageBitmap(getBitmapForOrientation(resources.configuration.orientation))
            invalidate()
        } else if (Preferences.Mock.mockOpacity == key) {
            imageAlpha = Preferences.Mock.getMockOpacity(10)
            invalidate()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Preferences.prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
        setImageBitmap(getBitmapForOrientation(resources.configuration.orientation))
        imageAlpha = Preferences.Mock.getMockOpacity(10)
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Preferences.prefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setImageBitmap(getBitmapForOrientation(resources.configuration.orientation))
    }

    private fun getBitmapForOrientation(orientation: Int): Bitmap? {
        return if (orientation == Configuration.ORIENTATION_PORTRAIT)
            getPortraitMockup()
        else
            getLandscapeMockup()
    }
}