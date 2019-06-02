package com.digitex.designertools.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.digitex.designertools.R
import com.digitex.designertools.designerApplication

object Preferences {

    private const val PREFERENCES_FILE = "com.digitex.designertools_preferences"

    val prefs: SharedPreferences by lazy {
        designerApplication.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
    }

    private val defGridLineColor by lazy {
        designerApplication.getColor(R.color.dualColorPickerDefaultPrimaryColor)
    }

    private val defKeyLineColor by lazy {
        designerApplication.getColor(R.color.dualColorPickerDefaultSecondaryColor)
    }

    private fun putBoolean(key: String, value: Boolean) = prefs.edit { putBoolean(key, value) }

    private fun getBoolean(key: String): Boolean = prefs.getBoolean(key, false)

    private fun putInt(key: String, value: Int) = prefs.edit { putInt(key, value) }

    private fun getInt(key: String, defValue: Int = 0): Int = prefs.getInt(key, defValue)

    private fun putString(key: String, value: String) = prefs.edit { putString(key, value) }

    private fun getString(key: String, defValue: String = ""): String = prefs.getString(key, defValue)!!

    object Grid {
        const val gridQsTile = "grid_qs_tile"
        const val showGrid = "grid_increments"
        const val showKeylines = "keylines"
        const val useCustomGridSize = "use_custom_grid_size"
        const val gridColumnSize = "grid_column_size"
        const val gridRowSize = "grid_row_size"
        const val gridLineColor = "grid_line_color"
        const val keylineColor = "keyline_color"
        const val gridOverlayActive = "grid_overlay_active"

        fun setGridQsTileEnabled(value: Boolean) = putBoolean(gridQsTile, value)
        fun getGridQsTileEnabled(): Boolean = getBoolean(gridQsTile)
        fun setShowGrid(value: Boolean) = putBoolean(showGrid, value)
        fun getShowGrid(): Boolean = getBoolean(showGrid)
        fun setShowKeylines(value: Boolean) = putBoolean(showKeylines, value)
        fun getShowKeylines(): Boolean = getBoolean(showKeylines)
        fun setUseCustomGridSize(value: Boolean) = putBoolean(useCustomGridSize, value)
        fun getUseCustomGridSize(): Boolean = getBoolean(useCustomGridSize)
        fun setGridColumnSize(value: Int) = putInt(gridColumnSize, value)
        fun getGridColumnSize(defValue: Int): Int = getInt(gridColumnSize, defValue)
        fun setGridRowSize(value: Int) = putInt(gridRowSize, value)
        fun getGridRowSize(defValue: Int): Int = getInt(gridRowSize, defValue)
        fun setGridLineColor(value: Int) = putInt(gridLineColor, value)
        fun getGridLineColor(defValue: Int = defGridLineColor): Int = getInt(gridLineColor, defValue)
        fun setKeylineColor(value: Int) = putInt(keylineColor, value)
        fun getKeylineColor(defValue: Int = defKeyLineColor): Int = getInt(keylineColor, defValue)
        fun setGridOverlayActive(value: Boolean) = putBoolean(gridOverlayActive, value)
        fun getGridOverlayActive(): Boolean = getBoolean(gridOverlayActive)
    }

    object Mock {
        const val mockOpacity = "mock_opacity"
        const val mockOverlayPortrait = "mockup_overlay_portrait"
        const val mockOverlayLandscape = "mock_overlay_landscape"
        const val mockOverlayActive = "mock_overlay_active"
        const val mockQsTile = "mock_qs_tile"

        fun setMockOpacity(value: Int) = putInt(mockOpacity, value)
        fun getMockOpacity(defValue: Int): Int = getInt(mockOpacity, defValue)
        fun setPortraitMockOverlay(value: String) = putString(mockOverlayPortrait, value)
        fun getPortraitMockOverlay(): String = getString(mockOverlayPortrait)
        fun setLandscapeMockOverlay(value: String) = putString(mockOverlayLandscape, value)
        fun getLandscapeMockOverlay(): String = getString(mockOverlayLandscape)
        fun setMockOverlayActive(value: Boolean) = putBoolean(mockOverlayActive, value)
        fun getMockOverlayActive(): Boolean = getBoolean(mockOverlayActive)
        fun setMockQsTileEnabled(value: Boolean) = putBoolean(mockQsTile, value)
        fun getMockQsTileEnabled(): Boolean = getBoolean(mockQsTile)
    }

    object ColorPicker {
        const val colorPickerQsTile = "color_picker_qs_tile"
        const val colorPickerActive = "color_picker_active"

        fun setColorPickerQsTileEnabled(value: Boolean) = putBoolean(colorPickerQsTile, value)
        fun getColorPickerQsTileEnabled(): Boolean = getBoolean(colorPickerQsTile)
        fun setColorPickerActive(value: Boolean) = putBoolean(colorPickerActive, value)
        fun getColorPickerActive(): Boolean = getBoolean(colorPickerActive)
    }

    object Screenshot {
        const val screenshotInfo = "screenshot_info"

        fun setScreenshotInfoEnabled(value: Boolean) = putBoolean(screenshotInfo, value)
        fun getScreenshotInfoEnabled(): Boolean = getBoolean(screenshotInfo)
    }
}