package com.digitex.designertools.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.View
import com.digitex.designertools.R

class MagnifierNodeView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var reticlePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val outlinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fillPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val clearPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var centerX: Float = 0.0f
    private var centerY: Float = 0.0f
    private var radius: Float = 0.0f
    private val reticleRadius: Float
    private val density: Float = resources.displayMetrics.density

    init {
        val twoDp = 2f * density
        reticlePaint.color = 0x50ffffff
        reticlePaint.strokeWidth = twoDp
        reticlePaint.style = Paint.Style.STROKE

        outlinePaint.color = -0x7f000001
        outlinePaint.strokeWidth = twoDp
        outlinePaint.style = Paint.Style.STROKE
        outlinePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)

        fillPaint.color = -0x80000000
        fillPaint.strokeWidth = twoDp
        fillPaint.style = Paint.Style.FILL_AND_STROKE
        fillPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DARKEN)

        clearPaint.color = 0
        clearPaint.strokeWidth = twoDp
        clearPaint.style = Paint.Style.FILL
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        reticleRadius = resources.getInteger(R.integer.color_picker_sample_width) / 2 + twoDp
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = Math.min(w, h) / 2.0f - density * 2f
        centerX = w / 2.0f
        centerY = h / 2.0f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, radius, fillPaint)
        canvas.drawCircle(centerX, centerY, radius, outlinePaint)
        canvas.drawCircle(centerX, centerY, reticleRadius, clearPaint)
        canvas.drawCircle(centerX, centerY, reticleRadius, reticlePaint)
    }

}