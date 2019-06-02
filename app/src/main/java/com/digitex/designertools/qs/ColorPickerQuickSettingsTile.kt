package com.digitex.designertools.qs

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.digitex.designertools.R
import com.digitex.designertools.utils.LaunchUtils
import com.digitex.designertools.utils.PreferenceUtils.ColorPickerPreferences
import cyanogenmod.app.CMStatusBarManager
import cyanogenmod.app.CustomTile

object ColorPickerQuickSettingsTile {
    private val TAG = ColorPickerQuickSettingsTile::class.java.simpleName
    const val ACTION_TOGGLE_STATE = "com.digitex.designertools.action.TOGGLE_COLOR_PICKER_STATE"
    const val ACTION_UNPUBLISH = "com.digitex.designertools.action.UNPUBLISH_COLOR_PICKER_TILE"
    private const val TILE_ID = 5000

    @JvmOverloads
    @JvmStatic
    fun publishColorPickerTile(context: Context, state: Int = OnOffTileState.STATE_OFF) {
        val intent = Intent(ACTION_TOGGLE_STATE)
        intent.putExtra(OnOffTileState.EXTRA_STATE, state)
        val pi = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        val iconResId = if (state == OnOffTileState.STATE_OFF)
            R.drawable.ic_qs_colorpicker_off
        else
            R.drawable.ic_qs_colorpicker_on
        val tile = CustomTile.Builder(context)
                .setOnClickIntent(pi)
                .setLabel(context.getString(R.string.color_picker_qs_tile_label))
                .setIcon(iconResId)
                .build()
        CMStatusBarManager.getInstance(context).publishTile(TAG, TILE_ID, tile)
        ColorPickerPreferences.setColorPickerQsTileEnabled(context, true)
    }

    @JvmStatic
    fun unpublishColorPickerTile(context: Context) {
        CMStatusBarManager.getInstance(context).removeTile(TAG, TILE_ID)
        ColorPickerPreferences.setColorPickerQsTileEnabled(context, false)
        val intent = Intent(ACTION_UNPUBLISH)
        context.sendBroadcast(intent)
    }

    class ClickBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ColorPickerPreferences.getColorPickerQsTileEnabled(context, false)) {
                val state = intent.getIntExtra(OnOffTileState.EXTRA_STATE, OnOffTileState.STATE_OFF)
                if (state == OnOffTileState.STATE_OFF) {
                    publishColorPickerTile(context, OnOffTileState.STATE_ON)
                    LaunchUtils.startColorPickerOrRequestPermission(context)
                } else {
                    publishColorPickerTile(context, OnOffTileState.STATE_OFF)
                    ColorPickerPreferences.setColorPickerActive(context, false)
                }
            }
        }
    }
}
