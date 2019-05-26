package com.digitex.designertools.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar

class VerticalSeekBar : SeekBar {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(h, w, oldh, oldw)
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.rotate(270f)
        canvas.translate((-height).toFloat(), 0f)
        super.onDraw(canvas)

        // Work around for known bug with Marshmallow where the enabled thumb is not drawn
        if (isEnabled && Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            drawThumb(canvas)
        }
    }

    private fun drawThumb(canvas: Canvas) {
        thumb?.apply {
            canvas.save()
            canvas.rotate(270f, bounds.exactCenterX(), bounds.exactCenterY())
            canvas.translate(0f, bounds.height() / 3f)
            draw(canvas)
            canvas.restore()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (isEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                    progress = max - (max * event.y / height).toInt()
                    onSizeChanged(width, height, 0, 0)
                }
            }
            true
        } else true
    }

}