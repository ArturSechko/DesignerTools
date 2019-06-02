package com.digitex.designertools.qs

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.digitex.designertools.R
import com.digitex.designertools.designerApplication
import com.digitex.designertools.utils.Preferences
import com.digitex.designertools.utils.cancelGridOverlay
import com.digitex.designertools.utils.launchGridOverlay
import com.digitex.designertools.utils.publishGridOverlayTile

import cyanogenmod.app.CMStatusBarManager
import cyanogenmod.app.CustomTile

object GridQuickSettingsTile {
    private val TAG = GridQuickSettingsTile::class.java.simpleName
    const val ACTION_TOGGLE_STATE = "com.digitex.designertools.action.TOGGLE_GRID_STATE"
    const val ACTION_UNPUBLISH = "com.digitex.designertools.action.UNPUBLISH_GRID_TILE"
    private const val TILE_ID = 1000

    fun publishGridTile(state: Int = OnOffTileState.STATE_OFF) {
        val intent = Intent(ACTION_TOGGLE_STATE)
        intent.putExtra(OnOffTileState.EXTRA_STATE, state)
        val pi = PendingIntent.getBroadcast(
                designerApplication,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        val iconResId = if (state == OnOffTileState.STATE_OFF)
            R.drawable.ic_qs_grid_off
        else
            R.drawable.ic_qs_grid_on
        val tile = CustomTile.Builder(designerApplication)
                .setOnClickIntent(pi)
                .setLabel(designerApplication.getString(R.string.grid_qs_tile_label))
                .setIcon(iconResId)
                .build()
        CMStatusBarManager.getInstance(designerApplication).publishTile(TAG, TILE_ID, tile)
        Preferences.Grid.setGridQsTileEnabled(true)
    }

    fun unpublishGridTile() {
        CMStatusBarManager.getInstance(designerApplication).removeTile(TAG, TILE_ID)
        Preferences.Grid.setGridQsTileEnabled(false)
        val intent = Intent(ACTION_UNPUBLISH)
        designerApplication.sendBroadcast(intent)
    }

    class ClickBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra(OnOffTileState.EXTRA_STATE, OnOffTileState.STATE_OFF)
            if (state == OnOffTileState.STATE_OFF) {
                publishGridOverlayTile(OnOffTileState.STATE_ON)
                launchGridOverlay()
            } else {
                publishGridOverlayTile(OnOffTileState.STATE_OFF)
                cancelGridOverlay()
            }
        }
    }
}
