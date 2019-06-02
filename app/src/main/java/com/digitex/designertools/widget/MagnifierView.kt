package com.digitex.designertools.widget

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.getSystemService
import com.digitex.designertools.R
import kotlinx.android.synthetic.main.color_picker_magnifier.view.*

class MagnifierView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val magnifyingLens: Drawable = context.getDrawable(R.drawable.loop_ring)!!
    private var pixels: Bitmap? = null
    private val bitmapPaint: Paint = Paint()
    private val gridPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pixelOutlinePaint: Paint = Paint()

    private var sourcePreviewRect: Rect = Rect()
    private val destinationPreviewRect: Rect = Rect()
    private var targetPixelOutline: RectF = RectF()

    private val insets: Point = Point()
    private val previewClipPath: Path = Path()

    private var centerPixelColor: Int = 0

    init {
        bitmapPaint.isAntiAlias = false
        bitmapPaint.isDither = true
        bitmapPaint.isFilterBitmap = false

        val dm = resources.displayMetrics
        gridPaint.color = -0x1000000
        gridPaint.alpha = 128
        gridPaint.style = Paint.Style.STROKE
        gridPaint.strokeWidth = 1f * dm.density
        gridPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DARKEN)

        pixelOutlinePaint.color = -0x1000000
        pixelOutlinePaint.style = Paint.Style.STROKE
        pixelOutlinePaint.strokeWidth = 2f * dm.density
        pixelOutlinePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DARKEN)

        val res = resources
        insets.set(
                res.getDimensionPixelSize(R.dimen.magnified_image_horizontal_inset),
                res.getDimensionPixelSize(R.dimen.magnified_image_vertical_inset)
        )

        val previewSize = res.getDimensionPixelSize(R.dimen.magnified_image_size)
        destinationPreviewRect.set(
                insets.x,
                insets.y,
                insets.x + previewSize,
                insets.y + previewSize
        )
        previewClipPath.addCircle(
                destinationPreviewRect.exactCenterX(),
                destinationPreviewRect.exactCenterY(),
                previewSize / 2f,
                Path.Direction.CCW
        )
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        colorValue.setOnClickListener {
            val cm = context.getSystemService<ClipboardManager>()!!
            val text = colorValue.text
            val primaryClip = cm.primaryClip
            if (primaryClip?.getItemAt(0) == null
                    || text != cm.primaryClip!!.getItemAt(0).coerceToText(context)) {
                val clip = ClipData.newPlainText("color", text)
                cm.primaryClip = clip
                Toast.makeText(context, R.string.color_copied_to_clipboard, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        magnifyingLens.setBounds(0, 0, w, h)
    }

    override fun dispatchDraw(canvas: Canvas) {
        magnifyingLens.draw(canvas)
        if (pixels != null) {
            canvas.clipPath(previewClipPath)
            canvas.drawBitmap(pixels, sourcePreviewRect, destinationPreviewRect, bitmapPaint)
            drawGrid(canvas)
            canvas.drawRect(targetPixelOutline, pixelOutlinePaint)
        }

        super.dispatchDraw(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        val stepSize = destinationPreviewRect.width() / sourcePreviewRect.width().toFloat()

        destinationPreviewRect.apply {
            for (x in (left + stepSize.toInt())..right step stepSize.toInt()) {
                canvas.drawLine(
                        x.toFloat(),
                        top.toFloat(),
                        x.toFloat(),
                        bottom.toFloat(),
                        gridPaint
                )
            }
            for (y in (top + stepSize.toInt())..bottom step stepSize.toInt()) {
                canvas.drawLine(
                        left.toFloat(),
                        y.toFloat(),
                        right.toFloat(),
                        y.toFloat(),
                        gridPaint
                )
            }
        }
    }

    fun setPixels(pixels: Bitmap) {
        this.pixels = pixels
        sourcePreviewRect.set(0, 0, pixels.width, pixels.height)
        centerPixelColor = pixels.getPixel(pixels.width / 2, pixels.height / 2)

        val pixelSize = destinationPreviewRect.width().toFloat() / pixels.width
        val x = (pixels.width - 1) / 2f * pixelSize
        val y = (pixels.height - 1) / 2f * pixelSize
        targetPixelOutline.set(
                destinationPreviewRect.left + x,
                destinationPreviewRect.top + y,
                destinationPreviewRect.left.toFloat() + x + pixelSize,
                destinationPreviewRect.top.toFloat() + y + pixelSize
        )

        colorValue.text = String.format("#%06X", centerPixelColor and 0x00ffffff)
        invalidate()
    }
}