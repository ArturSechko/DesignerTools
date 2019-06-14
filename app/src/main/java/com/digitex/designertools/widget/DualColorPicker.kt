package com.digitex.designertools.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.digitex.designertools.R
import com.digitex.designertools.ext.getDarkenedColor
import com.digitex.designertools.utils.Preferences

class DualColorPicker @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private companion object {
        private const val STROKE_WIDTH = 5f
        private const val COLOR_DARKEN_FACTOR = 0.8f
    }

    private val primaryFillPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private val secondaryFillPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private val primaryStrokePaint: Paint = Paint(primaryFillPaint)
    private val secondaryStrokePaint: Paint = Paint(secondaryFillPaint)
    private val leftHalfOval: RectF = RectF()
    private val rightHalfOval: RectF = RectF()

    var primaryColor
        get() = primaryFillPaint.color
        set(value) {
            primaryFillPaint.color = value
            primaryStrokePaint.color = value.getDarkenedColor(COLOR_DARKEN_FACTOR)
            invalidate()
        }
    var secondaryColor
        get() = secondaryFillPaint.color
        set(value) {
            secondaryFillPaint.color = value
            secondaryStrokePaint.color = value.getDarkenedColor(COLOR_DARKEN_FACTOR)
            invalidate()
        }

    init {
        context.withStyledAttributes(attrs, R.styleable.DualColorPicker) {
            val defPrimaryColor = if (isInEditMode)
                context.getColor(R.color.colorPrimary)
            else
                Preferences.Grid.getGridLineColor()

            val defSecondaryColor = if (isInEditMode)
                context.getColor(R.color.colorAccent)
            else
                Preferences.Grid.getKeylineColor()

            val primaryColor = getColor(R.styleable.DualColorPicker_primaryColor, defPrimaryColor)
            val secondaryColor = getColor(R.styleable.DualColorPicker_secondaryColor, defSecondaryColor)

            primaryFillPaint.style = Paint.Style.FILL
            primaryFillPaint.color = primaryColor
            primaryStrokePaint.style = Paint.Style.STROKE
            primaryStrokePaint.strokeWidth = STROKE_WIDTH
            primaryStrokePaint.color = primaryColor.getDarkenedColor(COLOR_DARKEN_FACTOR)

            secondaryFillPaint.style = Paint.Style.FILL
            secondaryFillPaint.color = secondaryColor
            secondaryStrokePaint.style = Paint.Style.STROKE
            secondaryStrokePaint.strokeWidth = STROKE_WIDTH
            secondaryStrokePaint.color = secondaryColor.getDarkenedColor(COLOR_DARKEN_FACTOR)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = Math.min(centerX, centerY) * 0.9f

        // erase everything
        canvas.drawColor(0)

        // draw the left half
        leftHalfOval.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        leftHalfOval.offset(-radius * 0.05f, 0f)
        canvas.drawArc(leftHalfOval, 90f, 180f, true, primaryFillPaint)
        leftHalfOval.inset(STROKE_WIDTH / 2, STROKE_WIDTH / 2)
        canvas.drawArc(leftHalfOval, 90f, 180f, false, primaryStrokePaint)
        canvas.drawLine(centerX - STROKE_WIDTH, centerY - radius + STROKE_WIDTH,
                centerX - STROKE_WIDTH, centerY + radius - STROKE_WIDTH, primaryStrokePaint)

        // draw the right half
        rightHalfOval.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        leftHalfOval.offset(radius * 0.05f, 0f)
        canvas.drawArc(rightHalfOval, 270f, 180f, true, secondaryFillPaint)
        rightHalfOval.inset(STROKE_WIDTH / 2, STROKE_WIDTH / 2)
        canvas.drawArc(rightHalfOval, 270f, 180f, false, secondaryStrokePaint)
        canvas.drawLine(centerX + STROKE_WIDTH / 2f, centerY - radius + STROKE_WIDTH,
                centerX + STROKE_WIDTH / 2f, centerY + radius - STROKE_WIDTH, secondaryStrokePaint)
    }

}