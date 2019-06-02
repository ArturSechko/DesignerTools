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
import android.graphics.Point
import android.os.IBinder
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.core.view.doOnPreDraw
import com.digitex.designertools.R
import com.digitex.designertools.designerApplication
import com.digitex.designertools.isAtLeastSdk
import com.digitex.designertools.overlays.view.MockOverlayView
import com.digitex.designertools.qs.MockQuickSettingsTile
import com.digitex.designertools.qs.OnOffTileState
import com.digitex.designertools.utils.NotificationUtils

class MockOverlay : Service() {

    private val windowManager: WindowManager = getSystemService()!!
    private lateinit var overlayView: MockOverlayView
    private lateinit var params: WindowManager.LayoutParams

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (MockQuickSettingsTile.ACTION_UNPUBLISH == action) {
                stopSelf()
            } else if (MockQuickSettingsTile.ACTION_TOGGLE_STATE == action) {
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
        designerApplication.isMockOverlayOn = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::overlayView.isInitialized) {
            hideOverlay()
        }
        unregisterReceiver(receiver)
        designerApplication.isMockOverlayOn = false
    }

    private fun setup() {
        val size = Point()
        windowManager.defaultDisplay.getRealSize(size)
        params = WindowManager.LayoutParams(
                size.x,
                size.y,
                if (isAtLeastSdk(26))
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSPARENT
        )
        overlayView = MockOverlayView(this)
        overlayView.alpha = 0f
        overlayView.doOnPreDraw {
            it.animate().alpha(1f)
        }
        windowManager.addView(overlayView, params)
        registerReceiver(receiver, IntentFilter(MockQuickSettingsTile.ACTION_TOGGLE_STATE).also {
            it.addAction(MockQuickSettingsTile.ACTION_UNPUBLISH)
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
        val channelId = NotificationUtils.createNotificationChannel(this, javaClass.simpleName, "Mockup overlay")
        val builder = NotificationCompat.Builder(this, channelId)
        val text = getString(if (actionIsHide)
            R.string.notif_content_hide_mock_overlay
        else
            R.string.notif_content_show_mock_overlay)
        return builder.setPriority(NotificationManagerCompat.IMPORTANCE_MIN)
                .setSmallIcon(if (actionIsHide) R.drawable.ic_qs_overlay_on else R.drawable.ic_qs_overlay_off)
                .setContentTitle(getString(R.string.mock_qs_tile_label))
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
        private val NOTIFICATION_ID = MockOverlay::class.java.hashCode()

        private const val ACTION_HIDE_OVERLAY = "hide_mock_overlay"
        private const val ACTION_SHOW_OVERLAY = "show_mock_overlay"
    }
}
