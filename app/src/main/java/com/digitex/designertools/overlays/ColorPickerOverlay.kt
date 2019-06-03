package com.digitex.designertools.overlays

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.*
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.core.graphics.createBitmap
import androidx.core.view.doOnPreDraw
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.dynamicanimation.animation.springAnimationOf
import androidx.dynamicanimation.animation.withSpringForceProperties
import com.digitex.designertools.R
import com.digitex.designertools.designerApplication
import com.digitex.designertools.ext.animate
import com.digitex.designertools.ext.doOnEnd
import com.digitex.designertools.ext.spring
import com.digitex.designertools.ext.springValueAnimation
import com.digitex.designertools.qs.ColorPickerQuickSettingsTile
import com.digitex.designertools.qs.OnOffTileState
import com.digitex.designertools.utils.createNotificationChannel
import com.digitex.designertools.utils.isAtLeastSdk
import com.digitex.designertools.utils.lerp
import com.digitex.designertools.widget.MagnifierNodeView
import com.digitex.designertools.widget.MagnifierView

@SuppressLint("ClickableViewAccessibility")
class ColorPickerOverlay : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var params: WindowManager.LayoutParams
    private lateinit var magnifierParams: WindowManager.LayoutParams

    private lateinit var mediaProjectionManager: MediaProjectionManager
    private lateinit var mediaProjection: MediaProjection
    private lateinit var virtualDisplay: VirtualDisplay
    private lateinit var imageReader: ImageReader

    private lateinit var magnifierView: MagnifierView
    private lateinit var magnifierNodeView: MagnifierNodeView
    private val previewArea: Rect = Rect()
    private var previewSampleWidth: Int = 0
    private var previewSampleHeight: Int = 0

    private var nodeToMagnifierDistance: Float = 0f
    private var angle = Math.PI.toFloat() * 1.5f

    private val lastPosition: PointF = PointF()
    private val startPosition: PointF = PointF()
    private var dampeningFactor: Float = 0f

    private var currentOrientation: Int = 0

    private val screenCaptureLock = Any()

    private var magnifierIsAnimating = false

    private val imageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        synchronized(screenCaptureLock) {
            val newImage = reader.acquireNextImage()
            if (newImage != null) {
                if (!magnifierIsAnimating) {
                    magnifierView.setPixels(getScreenBitmapRegion(newImage, previewArea))
                }
                newImage.close()
            }
        }
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ColorPickerQuickSettingsTile.ACTION_UNPUBLISH == action) {
                stopSelf()
            } else if (ColorPickerQuickSettingsTile.ACTION_TOGGLE_STATE == action) {
                val state = intent.getIntExtra(OnOffTileState.EXTRA_STATE, OnOffTileState.STATE_OFF)
                if (state == OnOffTileState.STATE_ON) {
                    stopSelf()
                }
            } else if (ACTION_HIDE_PICKER == action) {
                animateColorPickerOut {
                    removeOverlayViewsIfAttached()
                    teardownMediaProjection()
                    updateNotification(false)
                }
            } else if (ACTION_SHOW_PICKER == action) {
                addOverlayViewsIfDetached()
                setupMediaProjection()
                updateNotification(true)
                animateColorPickerIn()
            }
        }
    }

    private val onTouchListener = View.OnTouchListener { _, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> magnifierNodeView.isInvisible = true
            MotionEvent.ACTION_MOVE -> {
                val rawX = event.rawX
                val rawY = event.rawY
                val dx = magnifierParams.x + magnifierView.width / 2 - rawX
                val dy = magnifierParams.y + magnifierView.height / 2 - rawY
                angle = Math.atan2(dy.toDouble(), dx.toDouble()).toFloat()
                updateMagnifierViewPosition(rawX.toInt(), rawY.toInt(), angle)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> magnifierNodeView.isVisible = true
        }
        true
    }

    private val dampenedOnTouchListener = View.OnTouchListener { _, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastPosition.set(event.rawX, event.rawY)
                startPosition.set(params.x.toFloat(), params.y.toFloat())
            }
            MotionEvent.ACTION_MOVE -> {
                val rawX = event.rawX
                val rawY = event.rawY
                val dx = (rawX - lastPosition.x) / dampeningFactor
                val dy = (rawY - lastPosition.y) / dampeningFactor
                val x = startPosition.x + magnifierNodeView.width / 2 + dx
                val y = startPosition.y + magnifierNodeView.height / 2 + dy
                updateMagnifierViewPosition(x.toInt(), y.toInt(), angle)
            }
        }
        true
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        setup()
        designerApplication.isColorPickerOn = true
    }

    override fun onDestroy() {
        super.onDestroy()

        teardownMediaProjection()

        unregisterReceiver(receiver)
        imageReader.close()
        animateColorPickerOut {
            removeViewIfAttached(magnifierView)
            removeViewIfAttached(magnifierNodeView)
        }
        designerApplication.isColorPickerOn = false
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // recreate the media projection on orientation changes
        if (currentOrientation != newConfig.orientation) {
            recreateMediaProjection()
            currentOrientation = newConfig.orientation
        }
    }

    private fun setup() {
        windowManager = getSystemService()!!
        mediaProjectionManager = getSystemService()!!
        setupMediaProjection()

        currentOrientation = resources.configuration.orientation

        val magnifierWidth = resources.getDimensionPixelSize(R.dimen.picker_magnifying_ring_width)
        val magnifierHeight = resources.getDimensionPixelSize(R.dimen.picker_magnifying_ring_height)

        val nodeViewSize = resources.getDimensionPixelSize(R.dimen.picker_node_size)
        val dm = resources.displayMetrics

        params = WindowManager.LayoutParams(
                nodeViewSize,
                nodeViewSize,
                if (isAtLeastSdk(Build.VERSION_CODES.O))
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        magnifierParams = WindowManager.LayoutParams(
                magnifierWidth,
                magnifierHeight,
                if (isAtLeastSdk(Build.VERSION_CODES.O))
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        )
        magnifierParams.gravity = Gravity.TOP or Gravity.START

        val x = dm.widthPixels / 2
        val y = dm.heightPixels / 2
        params.x = x - nodeViewSize / 2
        params.y = y - nodeViewSize / 2

        magnifierParams.x = x - magnifierWidth / 2
        magnifierParams.y = params.y - (magnifierHeight + nodeViewSize / 2)

        magnifierView = View.inflate(this, R.layout.color_picker_magnifier, null) as MagnifierView
        magnifierView.setOnTouchListener(dampenedOnTouchListener)
        magnifierNodeView = MagnifierNodeView(this)
        magnifierNodeView.setOnTouchListener(onTouchListener)
        addOverlayViewsIfDetached()
        magnifierView.doOnPreDraw {
            animateColorPickerIn()
        }

        previewSampleWidth = resources.getInteger(R.integer.color_picker_sample_width)
        previewSampleHeight = resources.getInteger(R.integer.color_picker_sample_height)
        previewArea.set(
                x - previewSampleWidth / 2,
                y - previewSampleHeight / 2,
                x + previewSampleWidth / 2 + 1,
                y + previewSampleHeight / 2 + 1
        )

        nodeToMagnifierDistance = (Math.min(magnifierWidth, magnifierHeight) + nodeViewSize * 2) / 2f
        dampeningFactor = DAMPENING_FACTOR_DP * dm.density

        registerReceiver(receiver, IntentFilter(ColorPickerQuickSettingsTile.ACTION_TOGGLE_STATE).also {
            it.addAction(ColorPickerQuickSettingsTile.ACTION_UNPUBLISH)
            it.addAction(ACTION_HIDE_PICKER)
            it.addAction(ACTION_SHOW_PICKER)
        })
        startForeground(NOTIFICATION_ID, getPersistentNotification(true))
    }

    private fun removeViewIfAttached(v: View) {
        if (v.isAttachedToWindow) {
            windowManager.removeView(v)
        }
    }

    private fun removeOverlayViewsIfAttached() {
        removeViewIfAttached(magnifierView)
        removeViewIfAttached(magnifierNodeView)
    }

    private fun addOverlayViewsIfDetached() {
        if (!magnifierNodeView.isAttachedToWindow) {
            windowManager.addView(magnifierNodeView, params)
        }
        if (!magnifierView.isAttachedToWindow) {
            windowManager.addView(magnifierView, magnifierParams)
        }
    }

    private fun animateColorPickerIn() {
        magnifierNodeView.isInvisible = true

        val startX = magnifierParams.x + (magnifierParams.width - params.width) / 2
        val startY = magnifierParams.y + (magnifierParams.height - params.height) / 2
        val endX = params.x
        val endY = params.y
        params.x = startX
        params.y = startY
        windowManager.updateViewLayout(magnifierNodeView, params)

        val magnifierNodeAnim = springValueAnimation { animation ->
            params.x = lerp(startX, endX, animation).toInt()
            params.y = lerp(startY, endY, animation).toInt()
            windowManager.updateViewLayout(magnifierNodeView, params)
        }.doOnEnd { _, _, _, _ ->
            magnifierIsAnimating = false
        }.withSpringForceProperties {
            dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
        }

        magnifierView.apply {
            spring(SpringAnimation.SCALE_Y).animate(0f, 1f)
            spring(SpringAnimation.SCALE_X).doOnEnd { _, _, _, _ ->
                magnifierIsAnimating = true
                magnifierNodeView.isVisible = true
                magnifierNodeAnim.start()
            }.animate(0f, 1f)
        }
    }

    private fun animateColorPickerOut(endAction: () -> Unit) {
        val endX = magnifierParams.x + (magnifierParams.width - params.width) / 2
        val endY = magnifierParams.y + (magnifierParams.height - params.height) / 2
        val startX = params.x
        val startY = params.y
        windowManager.updateViewLayout(magnifierNodeView, params)

        springValueAnimation { animation ->
            params.x = lerp(startX, endX, animation).toInt()
            params.y = lerp(startY, endY, animation).toInt()
            windowManager.updateViewLayout(magnifierNodeView, params)
        }.doOnEnd { _, _, _, _ ->
            magnifierIsAnimating = false
            magnifierNodeView.isGone = true
            params.x = startX
            params.y = startY
            magnifierView.apply {
                spring(SpringAnimation.SCALE_Y).animateToFinalPosition(0f)
                spring(SpringAnimation.SCALE_X).doOnEnd { _, _, _, _ ->
                    endAction.invoke()
                }.animateToFinalPosition(0f)
            }
        }.start()
        magnifierIsAnimating = true
        magnifierNodeView.isVisible = true
    }

    private fun getScreenBitmapRegion(image: Image, region: Rect): Bitmap {
        val maxX = image.width - 1
        val maxY = image.height - 1
        val width = region.width()
        val height = region.height()
        val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val planes = image.planes
        val buffer = planes[0].buffer
        val rowStride = planes[0].rowStride
        val pixelStride = planes[0].pixelStride
        var color: Int
        var pixelX: Int
        var pixelY: Int
        for (y in 0 until height) {
            for (x in 0 until width) {
                pixelX = region.left + x
                pixelY = region.top + y
                color = if (pixelX in 0..maxX && pixelY >= 0 && pixelY <= maxY) {
                    val index = pixelY * rowStride + pixelX * pixelStride
                    buffer.position(index)
                    Color.argb(
                            255,
                            buffer.get().toInt() and 0xff,
                            buffer.get().toInt() and 0xff,
                            buffer.get().toInt() and 0xff
                    )
                } else 0
                bitmap.setPixel(x, y, color)
            }
        }
        return bitmap
    }

    private fun setupMediaProjection() {
        val dm = resources.displayMetrics
        val size = Point()
        windowManager.defaultDisplay.getRealSize(size)
        imageReader = ImageReader.newInstance(
                size.x,
                size.y,
                PixelFormat.RGBA_8888,
                2
        )
        imageReader.setOnImageAvailableListener(imageAvailableListener, Handler())
        mediaProjection = mediaProjectionManager.getMediaProjection(
                designerApplication.screenRecordResultCode,
                designerApplication.screenRecordResultData
        )
        virtualDisplay = mediaProjection.createVirtualDisplay(
                ColorPickerOverlay::class.java.simpleName,
                size.x,
                size.y,
                dm.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.surface,
                null,
                null
        )
    }

    private fun teardownMediaProjection() {
        virtualDisplay.release()
        mediaProjection.stop()
    }

    private fun recreateMediaProjection() {
        teardownMediaProjection()
        setupMediaProjection()
    }

    private fun updateNotification(actionIsHide: Boolean) {
        val nm = getSystemService<NotificationManager>()!!
        nm.notify(NOTIFICATION_ID, getPersistentNotification(actionIsHide))
    }

    private fun getPersistentNotification(actionIsHide: Boolean): Notification {
        val pi = PendingIntent.getBroadcast(
                this,
                0,
                Intent(if (actionIsHide) ACTION_HIDE_PICKER else ACTION_SHOW_PICKER),
                0
        )
        val channelId = createNotificationChannel(javaClass.simpleName, "Color Picker overlay")
        val builder = NotificationCompat.Builder(this, channelId)
        val text = if (actionIsHide) R.string.notif_content_hide_picker else R.string.notif_content_show_picker
        return builder.setPriority(NotificationManagerCompat.IMPORTANCE_MIN)
                .setSmallIcon(if (actionIsHide) R.drawable.ic_qs_colorpicker_on else R.drawable.ic_qs_colorpicker_off)
                .setContentTitle(getString(R.string.color_picker_qs_tile_label))
                .setContentText(getString(text))
                .setStyle(NotificationCompat.BigTextStyle().bigText(getString(text)))
                .setContentIntent(pi)
                .build()
    }

    private fun updateMagnifierViewPosition(x: Int, y: Int, angle: Float) {
        previewArea.left = x - previewSampleWidth / 2
        previewArea.top = y - previewSampleHeight / 2
        previewArea.right = x + previewSampleWidth / 2 + 1
        previewArea.bottom = y + previewSampleHeight / 2 + 1

        params.x = x - magnifierNodeView.width / 2
        params.y = y - magnifierNodeView.height / 2
        windowManager.updateViewLayout(magnifierNodeView, params)

        magnifierParams.x = (nodeToMagnifierDistance * Math.cos(angle.toDouble()) + x).toInt() - magnifierView.width / 2
        magnifierParams.y = (nodeToMagnifierDistance * Math.sin(angle.toDouble()) + y).toInt() - magnifierView.height / 2
        windowManager.updateViewLayout(magnifierView, magnifierParams)
    }

    companion object {
        private val NOTIFICATION_ID = ColorPickerOverlay::class.java.hashCode()

        private const val ACTION_HIDE_PICKER = "hide_picker"
        private const val ACTION_SHOW_PICKER = "show_picker"

        private const val DAMPENING_FACTOR_DP = 25.0f
    }
}
