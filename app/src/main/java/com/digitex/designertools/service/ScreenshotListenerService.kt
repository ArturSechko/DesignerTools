package com.digitex.designertools.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.digitex.designertools.R
import com.digitex.designertools.ui.DesignerToolsActivity
import com.digitex.designertools.utils.NotificationUtils
import com.digitex.designertools.utils.PreferenceUtils

class ScreenshotListenerService
    : Service(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var screenshotObserver: ScreenShotObserver

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        PreferenceUtils.getShardedPreferences(this).registerOnSharedPreferenceChangeListener(this)
        startForeground(42, getPersistentNotification())
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (!::screenshotObserver.isInitialized) {
            screenshotObserver = ScreenShotObserver(Handler())
            contentResolver.registerContentObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    true,
                    screenshotObserver
            )
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceUtils.getShardedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this)
        contentResolver.unregisterContentObserver(screenshotObserver)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (PreferenceUtils.ScreenshotPreferences.KEY_SCREENSHOT_INFO == key) {
            val enabled = PreferenceUtils.ScreenshotPreferences.getScreenshotInfoEnabled(this, false)
            if (!enabled) {
                stopSelf()
            }
        }
    }

    private fun getPersistentNotification(): Notification {
        return NotificationCompat.Builder(
                this,
                NotificationUtils.createNotificationChannel(
                        this,
                        javaClass.simpleName,
                        "Screenshot info"
                )
        ).apply {
            val text = getString(R.string.notif_content_screenshot_info)
            priority = NotificationManagerCompat.IMPORTANCE_MIN
            setSmallIcon(R.drawable.ic_qs_screenshotinfo_on)
            setContentTitle(getString(R.string.screenshot_qs_tile_label))
            setContentText(text)
            setStyle(NotificationCompat.BigTextStyle().bigText(text))
            setContentIntent(PendingIntent.getActivity(
                    this@ScreenshotListenerService,
                    0,
                    Intent(this@ScreenshotListenerService, DesignerToolsActivity::class.java),
                    0
            ))
        }.build()
    }

    private inner class ScreenShotObserver internal constructor(
            handler: Handler
    ) : ContentObserver(handler) {

        private val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        private val detectWindowsMs: Long = 1000

        private val externalContentUriMatcher = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()
        private val projection = arrayOf(
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN
        )

        override fun onChange(selfChange: Boolean, uri: Uri) {
            if (uri.toString().startsWith(externalContentUriMatcher)) {
                var cursor: Cursor? = null
                try {
                    cursor = contentResolver.query(
                            uri,
                            projection,
                            null,
                            null,
                            sortOrder
                    )
                    if (cursor != null && cursor.moveToFirst()) {
                        val path = cursor.getString(
                                cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                        )
                        val dateAdded = cursor.getLong(
                                cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)
                        )
                        val currentTime = System.currentTimeMillis()
                        if (path.toLowerCase().contains("screenshot") && Math.abs(currentTime - dateAdded) <= detectWindowsMs) {
                            val intent = Intent(
                                    this@ScreenshotListenerService,
                                    ScreenshotInfoService::class.java
                            )
                            intent.putExtra(ScreenshotInfoService.EXTRA_PATH, path)
                            startService(intent)
                        }
                    }
                } catch (e: Exception) {
                    Log.d(ScreenShotObserver::class.java.simpleName, "open cursor fail")
                } finally {
                    cursor?.close()
                }
            }
            super.onChange(selfChange, uri)
        }
    }
}