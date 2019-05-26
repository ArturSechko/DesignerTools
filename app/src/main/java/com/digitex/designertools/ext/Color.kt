package com.digitex.designertools.ext

import android.graphics.Color
import androidx.core.graphics.*

@androidx.annotation.ColorInt
internal fun Int.getDarkenedColor(darkenFactor: Float): Int {
    val a = alpha
    val r = (red * darkenFactor).toInt()
    val g = (green * darkenFactor).toInt()
    val b = (blue * darkenFactor).toInt()

    return Color.argb(a, r, g, b)
}