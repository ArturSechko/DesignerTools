package com.digitex.designertools.qs

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.digitex.designertools.R
import com.digitex.designertools.utils.LaunchUtils
import com.digitex.designertools.utils.PreferenceUtils.MockPreferences

import cyanogenmod.app.CMStatusBarManager
import cyanogenmod.app.CustomTile

object MockQuickSettingsTile {
    private val TAG = MockQuickSettingsTile::class.java.simpleName
    const val ACTION_TOGGLE_STATE = "com.digitex.designertools.action.TOGGLE_MOCK_STATE"
    const val ACTION_UNPUBLISH = "com.digitex.designertools.action.UNPUBLISH_MOCK_TILE"
    private const val TILE_ID = 2000

    @JvmOverloads
    @JvmStatic
    fun publishMockTile(context: Context, state: Int = OnOffTileState.STATE_OFF) {
        val intent = Intent(ACTION_TOGGLE_STATE)
        intent.putExtra(OnOffTileState.EXTRA_STATE, state)
        val pi = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        val iconResId = if (state == OnOffTileState.STATE_OFF)
            R.drawable.ic_qs_overlay_off
        else
            R.drawable.ic_qs_overlay_on
        val tile = CustomTile.Builder(context)
                .setOnClickIntent(pi)
                .setLabel(context.getString(R.string.mock_qs_tile_label))
                .setIcon(iconResId)
                .build()
        CMStatusBarManager.getInstance(context).publishTile(TAG, TILE_ID, tile)
        MockPreferences.setMockQsTileEnabled(context, true)
    }

    @JvmStatic
    fun unpublishMockTile(context: Context) {
        CMStatusBarManager.getInstance(context).removeTile(TAG, TILE_ID)
        MockPreferences.setMockQsTileEnabled(context, false)
        val intent = Intent(ACTION_UNPUBLISH)
        context.sendBroadcast(intent)
    }

    class ClickBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra(OnOffTileState.EXTRA_STATE, OnOffTileState.STATE_OFF)
            if (state == OnOffTileState.STATE_OFF) {
                LaunchUtils.publishMockOverlayTile(context, OnOffTileState.STATE_ON)
                LaunchUtils.launchMockOverlay(context)
            } else {
                LaunchUtils.publishMockOverlayTile(context, OnOffTileState.STATE_OFF)
                LaunchUtils.cancelMockOverlay(context)
            }
        }
    }
}
