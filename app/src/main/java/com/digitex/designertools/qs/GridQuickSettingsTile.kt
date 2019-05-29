package com.digitex.designertools.qs

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.digitex.designertools.R
import com.digitex.designertools.utils.LaunchUtils
import com.digitex.designertools.utils.PreferenceUtils.GridPreferences

import cyanogenmod.app.CMStatusBarManager
import cyanogenmod.app.CustomTile

object GridQuickSettingsTile {
    private val TAG = GridQuickSettingsTile::class.java.simpleName
    const val ACTION_TOGGLE_STATE = "com.digitex.designertools.action.TOGGLE_GRID_STATE"
    const val ACTION_UNPUBLISH = "com.digitex.designertools.action.UNPUBLISH_GRID_TILE"
    private const val TILE_ID = 1000

    @JvmOverloads
    @JvmStatic
    fun publishGridTile(context: Context, state: Int = OnOffTileState.STATE_OFF) {
        val intent = Intent(ACTION_TOGGLE_STATE)
        intent.putExtra(OnOffTileState.EXTRA_STATE, state)
        val pi = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        val iconResId = if (state == OnOffTileState.STATE_OFF)
            R.drawable.ic_qs_grid_off
        else
            R.drawable.ic_qs_grid_on
        val tile = CustomTile.Builder(context)
                .setOnClickIntent(pi)
                .setLabel(context.getString(R.string.grid_qs_tile_label))
                .setIcon(iconResId)
                .build()
        CMStatusBarManager.getInstance(context).publishTile(TAG, TILE_ID, tile)
        GridPreferences.setGridQsTileEnabled(context, true)
    }

    @JvmStatic
    fun unpublishGridTile(context: Context) {
        CMStatusBarManager.getInstance(context).removeTile(TAG, TILE_ID)
        GridPreferences.setGridQsTileEnabled(context, false)
        val intent = Intent(ACTION_UNPUBLISH)
        context.sendBroadcast(intent)
    }

    class ClickBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra(OnOffTileState.EXTRA_STATE, OnOffTileState.STATE_OFF)
            if (state == OnOffTileState.STATE_OFF) {
                LaunchUtils.publishGridOverlayTile(context, OnOffTileState.STATE_ON)
                LaunchUtils.launchGridOverlay(context)
            } else {
                LaunchUtils.publishGridOverlayTile(context, OnOffTileState.STATE_OFF)
                LaunchUtils.cancelGridOverlay(context)
            }
        }
    }
}
