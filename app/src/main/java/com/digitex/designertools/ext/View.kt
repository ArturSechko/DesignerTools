package com.digitex.designertools.ext

import android.graphics.Bitmap
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.drawToBitmap

fun View.renderToBitmap(): Bitmap {
    layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
    )
    measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    layout(0, 0, measuredWidth, measuredHeight)

    return drawToBitmap()
}