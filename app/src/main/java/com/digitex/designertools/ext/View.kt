package com.digitex.designertools.ext

import android.view.View
import android.view.WindowInsets
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.dynamicanimation.animation.withSpringForceProperties

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

fun View.doOnAttachToWindow(action: (view: View) -> Unit) {
    addOnAttachStateChangeListener(onAttach = action)
}

fun View.doOnDettachFromWindow(action: (view: View) -> Unit) {
    addOnAttachStateChangeListener(onDetach = action)
}

private inline fun View.addOnAttachStateChangeListener(
        crossinline onAttach: (view: View) -> Unit = {},
        crossinline onDetach: (view: View) -> Unit = {}
) = addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
    override fun onViewAttachedToWindow(view: View) {
        view.removeOnAttachStateChangeListener(this)
        onAttach(view)
    }

    override fun onViewDetachedFromWindow(view: View) {
        view.removeOnAttachStateChangeListener(this)
        onDetach(view)
    }
})

fun View.doOnApplyWindowInsets(action: (View, WindowInsets, InitialPadding) -> Unit) {
    val initialPadding = InitialPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
    // Set an actual OnApplyWindowInsetsListener which proxies to the given
    // lambda, also passing in the original padding state
    setOnApplyWindowInsetsListener { view, insets ->
        action(view, insets, initialPadding)
        // Always return the insets, so that children can also use them
        insets
    }
    // request some insets
    requestApplyInsetsWhenAttached()
}

data class InitialPadding(val left: Int, val top: Int,
                          val right: Int, val bottom: Int)

private fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        doOnAttachToWindow {
            requestApplyInsets()
        }
    }
}