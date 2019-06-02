package com.digitex.designertools.ext

import android.graphics.Bitmap
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.drawToBitmap
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.dynamicanimation.animation.withSpringForceProperties

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

fun View.spring(
        property: DynamicAnimation.ViewProperty,
        finalPosition: Float = 0f,
        stiffness: Float = SpringForce.STIFFNESS_MEDIUM,
        dampingRatio: Float = SpringForce.DAMPING_RATIO_NO_BOUNCY
): SpringAnimation {
    val key = property.hashCode()
    var animation = getTag(key) as? SpringAnimation?
    if (animation == null) {
        animation = SpringAnimation(this, property)
        setTag(key, animation)
    }
    return animation.withSpringForceProperties {
        this.stiffness = stiffness
        this.dampingRatio = dampingRatio
        this.finalPosition = finalPosition
    }
}