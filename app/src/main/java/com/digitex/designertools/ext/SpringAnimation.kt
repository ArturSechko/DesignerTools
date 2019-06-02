package com.digitex.designertools.ext

import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.springAnimationOf

fun SpringAnimation.animate(
        from: Float,
        to: Float
) = setStartValue(from).animateToFinalPosition(to)

inline fun SpringAnimation.doOnEnd(
        crossinline action: (
                animation: DynamicAnimation<out DynamicAnimation<*>>?,
                canceled: Boolean,
                value: Float,
                velocity: Float
        ) -> Unit
): SpringAnimation = addEndListener(object : DynamicAnimation.OnAnimationEndListener {
    override fun onAnimationEnd(animation: DynamicAnimation<out DynamicAnimation<*>>?, canceled: Boolean, value: Float, velocity: Float) {
        removeEndListener(this)
        action.invoke(animation, canceled, value, velocity)
    }
})

inline fun SpringAnimation.doOnUpdate(
        crossinline action: (
                animation: DynamicAnimation<out DynamicAnimation<*>>?,
                value: Float,
                velocity: Float
        ) -> Unit
): SpringAnimation = addUpdateListener(object : DynamicAnimation.OnAnimationUpdateListener {
    override fun onAnimationUpdate(animation: DynamicAnimation<out DynamicAnimation<*>>?, value: Float, velocity: Float) {
        removeUpdateListener(this)
        action.invoke(animation, value, velocity)
    }
})

inline fun springValueAnimation(
        crossinline onFrame: (animation: Float) -> Unit
): SpringAnimation = springAnimationOf(
        setter = { value ->
            onFrame(value / 100f)
        },
        getter = { 0f },
        finalPosition = 100f
)