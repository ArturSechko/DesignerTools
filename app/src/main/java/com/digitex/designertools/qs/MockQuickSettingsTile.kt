package com.digitex.designertools.qs

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.digitex.designertools.R
import com.digitex.designertools.designerApplication
import com.digitex.designertools.utils.Preferences
import com.digitex.designertools.utils.cancelMockOverlay
import com.digitex.designertools.utils.launchMockOverlay
import com.digitex.designertools.utils.publishMockOverlayTile

import cyanogenmod.app.CMStatusBarManager
import cyanogenmod.app.CustomTile

object MockQuickSettingsTile {
    private val TAG = MockQuickSettingsTile::class.java.simpleName
    const val ACTION_TOGGLE_STATE = "com.digitex.designertools.action.TOGGLE_MOCK_STATE"
    const val ACTION_UNPUBLISH = "com.digitex.designertools.action.UNPUBLISH_MOCK_TILE"
    private const val TILE_ID = 2000

    fun publishMockTile(state: Int = OnOffTileState.STATE_OFF) {
        val intent = Intent(ACTION_TOGGLE_STATE)
        intent.putExtra(OnOffTileState.EXTRA_STATE, state)
        val pi = PendingIntent.getBroadcast(
                designerApplication,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        val iconResId = if (state == OnOffTileState.STATE_OFF)
            R.drawable.ic_qs_overlay_off
        else
            R.drawable.ic_qs_overlay_on
        val tile = CustomTile.Builder(designerApplication)
                .setOnClickIntent(pi)
                .setLabel(designerApplication.getString(R.string.mock_qs_tile_label))
                .setIcon(iconResId)
                .build()
        CMStatusBarManager.getInstance(designerApplication).publishTile(TAG, TILE_ID, tile)
        Preferences.Mock.setMockQsTileEnabled(true)
    }

    fun unpublishMockTile() {
        CMStatusBarManager.getInstance(designerApplication).removeTile(TAG, TILE_ID)
        Preferences.Mock.setMockQsTileEnabled(false)
        val intent = Intent(ACTION_UNPUBLISH)
        designerApplication.sendBroadcast(intent)
    }

    class ClickBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra(OnOffTileState.EXTRA_STATE, OnOffTileState.STATE_OFF)
            if (state == OnOffTileState.STATE_OFF) {
                publishMockOverlayTile(OnOffTileState.STATE_ON)
                launchMockOverlay()
            } else {
                publishMockOverlayTile(OnOffTileState.STATE_OFF)
                cancelMockOverlay()
            }
        }
    }
}
