package com.digitex.designertools.overlays.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.view.View
import com.digitex.designertools.R
import com.digitex.designertools.utils.Preferences

internal class GridOverlayView(context: Context) : View(context) {

    private val gridPaint: Paint = Paint()
    private val keylinePaint: Paint = Paint()
    private val firstKeylineRect: RectF = RectF()
    private val secondKeylineRect: RectF = RectF()
    private val thirdKeylineRect: RectF = RectF()
    private val horizontalGridMarkerLeft: Drawable
    private val horizontalMarkerLeft: Drawable
    private val horizontalMarkerRight: Drawable
    private val verticalMarker: Drawable

    private val verticalGridMarkerBounds: Rect = Rect()
    private val horizontalGridMarkerLeftBounds: Rect = Rect()
    private val horizontalGridMarkerRightBounds: Rect = Rect()
    private val firstKeylineMarkerBounds: Rect = Rect()
    private val secondKeylineMarkerBounds: Rect = Rect()
    private val thirdKeylineMarkerBounds: Rect = Rect()

    private var showGrid = Preferences.Grid.getShowGrid()
    private var showKeylines = Preferences.Grid.getShowKeylines()

    private val density: Float = resources.displayMetrics.density
    private val gridLineWidth: Float = density
    private var columnSize: Float = 0.toFloat()
    private var rowSize: Float = 0.toFloat()
    private val keylineWidth: Float = 1.5f * density

    init {
        gridPaint.color = Preferences.Grid.getGridLineColor()
        gridPaint.strokeWidth = gridLineWidth

        keylinePaint.color = Preferences.Grid.getKeylineColor()

        horizontalGridMarkerLeft = context.getDrawable(R.drawable.ic_marker_horiz_left)!!.mutate()
        horizontalMarkerLeft = context.getDrawable(R.drawable.ic_marker_horiz_left)!!
        horizontalMarkerRight = context.getDrawable(R.drawable.ic_marker_horiz_right)!!
        verticalMarker = context.getDrawable(R.drawable.ic_marker_vert)!!

        val useCustom = Preferences.Grid.getUseCustomGridSize()
        val defColumnSize = resources.getInteger(R.integer.default_column_size)
        val defRowSize = resources.getInteger(R.integer.default_row_size)
        columnSize = density * if (!useCustom) defColumnSize else Preferences.Grid.getGridColumnSize(defColumnSize)
        rowSize = density * if (!useCustom) defRowSize else Preferences.Grid.getGridRowSize(defRowSize)
    }

    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        if (Preferences.Grid.showGrid == key) {
            val enabled = prefs.getBoolean(Preferences.Grid.showGrid, false)
            if (showGrid != enabled) {
                showGrid = enabled
                invalidate()
            }
        } else if (Preferences.Grid.showKeylines == key) {
            val enabled = prefs.getBoolean(Preferences.Grid.showKeylines, false)
            if (enabled != showKeylines) {
                showKeylines = enabled
                invalidate()
            }
        } else if (Preferences.Grid.gridColumnSize == key) {
            columnSize = density * Preferences.Grid.getGridColumnSize(resources.getInteger(R.integer.default_column_size))
            invalidate()
        } else if (Preferences.Grid.gridRowSize == key) {
            rowSize = density * Preferences.Grid.getGridRowSize(resources.getInteger(R.integer.default_row_size))
            invalidate()
        } else if (Preferences.Grid.gridLineColor == key) {
            gridPaint.color = Preferences.Grid.getGridLineColor()
            invalidate()
        } else if (Preferences.Grid.keylineColor == key) {
            keylinePaint.color = Preferences.Grid.getKeylineColor()
            invalidate()
        } else if (Preferences.Grid.useCustomGridSize == key) {
            val useCustom = Preferences.Grid.getUseCustomGridSize()
            val defColumnSize = resources.getInteger(R.integer.default_column_size)
            val defRowSize = resources.getInteger(R.integer.default_row_size)
            columnSize = density * if (!useCustom) defColumnSize else Preferences.Grid.getGridColumnSize(defColumnSize)
            rowSize = density * if (!useCustom) defRowSize else Preferences.Grid.getGridRowSize(defRowSize)
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        drawGridLines(canvas)
        if (showKeylines) drawKeylines(canvas)

        drawGridMarkers(canvas)
        if (showKeylines) drawKeylineMarkers(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val dm = resources.displayMetrics

        var width = (10 * dm.density).toInt()
        var height = (6 * dm.density).toInt()
        var x = (24 * dm.density).toInt()
        var y = 0
        verticalGridMarkerBounds.set(x, y, x + width, y + height)
        val temp = height
        height = width
        width = temp
        x = 0
        y = (8 * dm.density).toInt()
        horizontalGridMarkerLeftBounds.set(x, y, x + width, y + height)
        x = dm.widthPixels - (width - 1)
        horizontalGridMarkerRightBounds.set(x, y, x + width, y + height)

        x = (16 * dm.density).toInt()
        firstKeylineMarkerBounds.set(x, y, x + width, y + height)
        x = (72 * dm.density).toInt()
        secondKeylineMarkerBounds.set(x, y, x + width, y + height)
        x = dm.widthPixels - (16 * dm.density).toInt()
        thirdKeylineMarkerBounds.set(x, y, x + width, y + height)

        firstKeylineRect.set(0f, 0f, 16 * dm.density, dm.heightPixels.toFloat())
        secondKeylineRect.set(56 * dm.density, 0f, 72 * dm.density, dm.heightPixels.toFloat())
        thirdKeylineRect.set(dm.widthPixels - 16 * dm.density, 0f, dm.widthPixels.toFloat(), dm.heightPixels.toFloat())
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Preferences.prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Preferences.prefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    private fun drawGridLines(canvas: Canvas) = canvas.apply {
        for (x in 0..width step columnSize.toInt()) {
            drawLine(x.toFloat(), 0f, x.toFloat(), (height - 1).toFloat(), gridPaint)
        }
        for (y in 0..height step rowSize.toInt()) {
            drawLine(0f, y.toFloat(), (width - 1).toFloat(), y.toFloat(), gridPaint)
        }
    }

    private fun drawGridMarkers(canvas: Canvas) {
        verticalMarker.setTint(gridPaint.color)
        verticalMarker.bounds = verticalGridMarkerBounds
        verticalMarker.draw(canvas)
        horizontalGridMarkerLeft.setTint(gridPaint.color)
        horizontalGridMarkerLeft.bounds = horizontalGridMarkerLeftBounds
        horizontalGridMarkerLeft.draw(canvas)
        horizontalMarkerRight.setTint(gridPaint.color)
        horizontalMarkerRight.bounds = horizontalGridMarkerRightBounds
        horizontalMarkerRight.draw(canvas)
    }

    private fun drawKeylines(canvas: Canvas) = canvas.apply {
        val alpha = keylinePaint.alpha
        // draw rects first
        keylinePaint.alpha = (0.5f * alpha).toInt()
        drawRect(firstKeylineRect, keylinePaint)
        drawRect(secondKeylineRect, keylinePaint)
        drawRect(thirdKeylineRect, keylinePaint)

        // draw lines next
        keylinePaint.alpha = alpha
        val stroke = keylinePaint.strokeWidth
        keylinePaint.strokeWidth = keylineWidth
        drawLine(firstKeylineRect.right, 0f, firstKeylineRect.right, height.toFloat(), keylinePaint)
        drawLine(secondKeylineRect.right, 0f, secondKeylineRect.right, height.toFloat(), keylinePaint)
        drawLine(thirdKeylineRect.left, 0f, thirdKeylineRect.left, height.toFloat(), keylinePaint)
        keylinePaint.strokeWidth = stroke
    }

    private fun drawKeylineMarkers(canvas: Canvas) {
        horizontalMarkerLeft.setTint(keylinePaint.color)
        horizontalMarkerLeft.bounds = firstKeylineMarkerBounds
        horizontalMarkerLeft.draw(canvas)
        horizontalMarkerLeft.bounds = secondKeylineMarkerBounds
        horizontalMarkerLeft.draw(canvas)
        horizontalMarkerLeft.bounds = thirdKeylineMarkerBounds
        horizontalMarkerLeft.draw(canvas)
    }
}