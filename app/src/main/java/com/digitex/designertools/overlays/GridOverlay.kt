package com.digitex.designertools.overlays

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.core.view.doOnPreDraw
import com.digitex.designertools.R
import com.digitex.designertools.designerApplication
import com.digitex.designertools.isAtLeastSdk
import com.digitex.designertools.overlays.view.GridOverlayView
import com.digitex.designertools.qs.GridQuickSettingsTile
import com.digitex.designertools.qs.OnOffTileState
import com.digitex.designertools.utils.NotificationUtils

class GridOverlay : Service() {

    private val windowManager: WindowManager = getSystemService()!!
    private lateinit var overlayView: GridOverlayView
    private lateinit var params: WindowManager.LayoutParams

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (GridQuickSettingsTile.ACTION_UNPUBLISH == action) {
                stopSelf()
            } else if (GridQuickSettingsTile.ACTION_TOGGLE_STATE == action) {
                val state = intent.getIntExtra(OnOffTileState.EXTRA_STATE, OnOffTileState.STATE_OFF)
                if (state == OnOffTileState.STATE_ON) {
                    stopSelf()
                }
            } else if (ACTION_HIDE_OVERLAY == action) {
                hideOverlay { updateNotification(false) }
            } else if (ACTION_SHOW_OVERLAY == action) {
                showOverlay()
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        setup()
        designerApplication.isGridOverlayOn = true
    }

    override fun onDestroy() {
        super.onDestroy()
        hideOverlay()
        unregisterReceiver(receiver)
        designerApplication.isGridOverlayOn = false
    }

    private fun setup() {
        params = WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                if (isAtLeastSdk(26))
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        )
        overlayView = GridOverlayView(this)
        overlayView.alpha = 0f
        overlayView.doOnPreDraw {
            it.animate().alpha(1f)
        }
        windowManager.addView(overlayView, params)
        registerReceiver(receiver, IntentFilter(GridQuickSettingsTile.ACTION_TOGGLE_STATE).also {
            it.addAction(GridQuickSettingsTile.ACTION_UNPUBLISH)
            it.addAction(ACTION_HIDE_OVERLAY)
            it.addAction(ACTION_SHOW_OVERLAY)
        })
        startForeground(NOTIFICATION_ID, getPersistentNotification(true))
    }

    private fun updateNotification(actionIsHide: Boolean) {
        val nm = getSystemService<NotificationManager>()!!
        nm.notify(NOTIFICATION_ID, getPersistentNotification(actionIsHide))
    }

    private fun getPersistentNotification(actionIsHide: Boolean): Notification {
        val pi = PendingIntent.getBroadcast(
                this,
                0,
                Intent(if (actionIsHide) ACTION_HIDE_OVERLAY else ACTION_SHOW_OVERLAY),
                0
        )
        val channelId = NotificationUtils.createNotificationChannel(this, javaClass.simpleName, "Grid overlay")
        val builder = NotificationCompat.Builder(this, channelId)
        val text = getString(if (actionIsHide)
            R.string.notif_content_hide_grid_overlay
        else
            R.string.notif_content_show_grid_overlay)
        return builder.setPriority(NotificationManagerCompat.IMPORTANCE_MIN)
                .setSmallIcon(if (actionIsHide) R.drawable.ic_qs_grid_on else R.drawable.ic_qs_grid_off)
                .setContentTitle(getString(R.string.grid_qs_tile_label))
                .setContentText(text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setContentIntent(pi)
                .build()
    }

    private fun showOverlay() {
        windowManager.addView(overlayView, params)
        updateNotification(true)
        overlayView.animate().alpha(1f)
    }

    private fun hideOverlay(endAction: () -> Unit = {}) {
        overlayView.animate().alpha(0f).withEndAction {
            overlayView.alpha = 0f
            if (overlayView.isAttachedToWindow) {
                windowManager.removeView(overlayView)
            }
            endAction.invoke()
        }
    }

    companion object {
        private val NOTIFICATION_ID = GridOverlay::class.java.hashCode()

        private const val ACTION_HIDE_OVERLAY = "hide_grid_overlay"
        private const val ACTION_SHOW_OVERLAY = "show_grid_overlay"
    }
}
