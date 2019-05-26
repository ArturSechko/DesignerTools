package com.digitex.designertools.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Dimension
import com.digitex.designertools.R

class GridPreview @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private companion object {
        // default line width in dp
        private const val DEFAULT_LINE_WIDTH = 1f
        // default column size in dp
        private const val DEFAULT_COLUMN_SIZE = 8
        // default row size in dp
        private const val DEFAULT_ROW_SIZE = 8
        private const val BACKGROUND_COLOR = 0x1f000000
    }

    var columnSizeDp: Int = DEFAULT_COLUMN_SIZE
        set(@Dimension(unit = Dimension.DP) value) {
            field = value
            columnSize = value * density
            invalidate()
        }
    var rowSizeDp: Int = DEFAULT_ROW_SIZE
        set(@Dimension(unit = Dimension.DP) value) {
            field = value
            rowSize = value * density
            invalidate()
        }
    private val density: Float = resources.displayMetrics.density
    private val gridLineWidth: Float = DEFAULT_LINE_WIDTH * density
    private var columnSize: Float = columnSizeDp * density
    private var rowSize: Float = rowSizeDp * density

    private val gridLinePaint: Paint = Paint()
    private val gridSizeTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)

    init {
        gridLinePaint.color = context.getColor(R.color.colorGridOverlayCardTint)
        gridLinePaint.strokeWidth = gridLineWidth

        gridSizeTextPaint.textSize = resources.getDimensionPixelSize(R.dimen.grid_preview_text_size).toFloat()
        gridSizeTextPaint.color = BACKGROUND_COLOR
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(BACKGROUND_COLOR)

        for (x in columnSize.toInt()..width step columnSize.toInt()) {
            canvas.drawLine(x.toFloat(), 0f, x.toFloat(), height.toFloat(), gridLinePaint)
        }

        for (y in rowSize.toInt()..height step rowSize.toInt()) {
            canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), gridLinePaint)
        }

        val text = String.format("%d x %d", columnSizeDp, rowSizeDp)
        val bounds = Rect()
        gridSizeTextPaint.getTextBounds(text, 0, text.length, bounds)
        canvas.drawText(text, (width - bounds.width()) / 2f, (height + bounds.height()) / 2f, gridSizeTextPaint)
    }

}