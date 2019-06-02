package com.digitex.designertools.qs

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.digitex.designertools.R
import com.digitex.designertools.designerApplication
import com.digitex.designertools.utils.Preferences
import com.digitex.designertools.utils.startColorPickerOrRequestPermission
import cyanogenmod.app.CMStatusBarManager
import cyanogenmod.app.CustomTile

object ColorPickerQuickSettingsTile {
    private val TAG = ColorPickerQuickSettingsTile::class.java.simpleName
    const val ACTION_TOGGLE_STATE = "com.digitex.designertools.action.TOGGLE_COLOR_PICKER_STATE"
    const val ACTION_UNPUBLISH = "com.digitex.designertools.action.UNPUBLISH_COLOR_PICKER_TILE"
    private const val TILE_ID = 5000

    fun publishColorPickerTile(state: Int = OnOffTileState.STATE_OFF) {
        val intent = Intent(ACTION_TOGGLE_STATE)
        intent.putExtra(OnOffTileState.EXTRA_STATE, state)
        val pi = PendingIntent.getBroadcast(
                designerApplication,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        val iconResId = if (state == OnOffTileState.STATE_OFF)
            R.drawable.ic_qs_colorpicker_off
        else
            R.drawable.ic_qs_colorpicker_on
        val tile = CustomTile.Builder(designerApplication)
                .setOnClickIntent(pi)
                .setLabel(designerApplication.getString(R.string.color_picker_qs_tile_label))
                .setIcon(iconResId)
                .build()
        CMStatusBarManager.getInstance(designerApplication).publishTile(TAG, TILE_ID, tile)
        Preferences.ColorPicker.setColorPickerQsTileEnabled(true)
    }

    fun unpublishColorPickerTile() {
        CMStatusBarManager.getInstance(designerApplication).removeTile(TAG, TILE_ID)
        Preferences.ColorPicker.setColorPickerQsTileEnabled(false)
        val intent = Intent(ACTION_UNPUBLISH)
        designerApplication.sendBroadcast(intent)
    }

    class ClickBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Preferences.ColorPicker.getColorPickerQsTileEnabled()) {
                val state = intent.getIntExtra(OnOffTileState.EXTRA_STATE, OnOffTileState.STATE_OFF)
                if (state == OnOffTileState.STATE_OFF) {
                    publishColorPickerTile(OnOffTileState.STATE_ON)
                    startColorPickerOrRequestPermission()
                } else {
                    publishColorPickerTile(OnOffTileState.STATE_OFF)
                    Preferences.ColorPicker.setColorPickerActive(false)
                }
            }
        }
    }
}
