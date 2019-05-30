package com.digitex.designertools.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.doOnLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.digitex.designertools.R
import kotlinx.android.synthetic.main.activity_credits.*

class CreditsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_credits)
    }

    private fun circularReveal(view: View = rootView) {
        val cx = view.width / 2
        val cy = view.height / 2

        val finalRadius = Math.max(view.width, view.height).toFloat()

        val circularReveal =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius)
        circularReveal.duration = resources.getInteger(
                R.integer.credits_circular_reveal_duration).toLong()

        view.isVisible = true
        circularReveal.interpolator = AccelerateDecelerateInterpolator()
        circularReveal.doOnEnd { animateContent() }
        circularReveal.start()
    }

    override fun onResume() {
        super.onResume()
        rootView.isInvisible = true

        if (rootView.viewTreeObserver.isAlive) {
            rootView.doOnLayout {
                circularReveal()
            }
        }
    }

    private fun animateContent() {
        avatar1.scaleX = 0f
        avatar1.scaleY = 0f
        avatar1.isVisible = true
        text1.alpha = 0f
        text1.isVisible = true
        avatar2.scaleX = 0f
        avatar2.scaleY = 0f
        avatar2.isVisible = true
        text2.alpha = 0f
        text2.isVisible = true
        avatar3.scaleX = 0f
        avatar3.scaleY = 0f
        avatar3.isVisible = true
        text3.alpha = 0f
        text3.isVisible = true
        avatar4.scaleX = 0f
        avatar4.scaleY = 0f
        avatar4.isVisible = true
        text4.alpha = 0f
        text4.isVisible = true

        val interpolator = FastOutSlowInInterpolator()
        val duration = 375L
        val delay = duration / 3

        val anim1 = AnimatorSet()
        anim1.play(ObjectAnimator.ofFloat(avatar1, "scaleX", 1f))
                .with(ObjectAnimator.ofFloat(avatar1, "scaleY", 1f))
                .with(ObjectAnimator.ofFloat(text1, "alpha", 1f))
        anim1.duration = duration
        anim1.interpolator = interpolator
        val anim2 = AnimatorSet()
        anim2.play(ObjectAnimator.ofFloat(avatar2, "scaleX", 1f))
                .with(ObjectAnimator.ofFloat(avatar2, "scaleY", 1f))
                .with(ObjectAnimator.ofFloat(text2, "alpha", 1f))
        anim2.duration = duration
        anim2.interpolator = interpolator
        anim2.startDelay = delay
        val anim3 = AnimatorSet()
        anim3.play(ObjectAnimator.ofFloat(avatar3, "scaleX", 1f))
                .with(ObjectAnimator.ofFloat(avatar3, "scaleY", 1f))
                .with(ObjectAnimator.ofFloat(text3, "alpha", 1f))
        anim3.duration = duration
        anim3.interpolator = interpolator
        anim3.startDelay = delay * 2
        val anim4 = AnimatorSet()
        anim4.play(ObjectAnimator.ofFloat(avatar4, "scaleX", 1f))
                .with(ObjectAnimator.ofFloat(avatar4, "scaleY", 1f))
                .with(ObjectAnimator.ofFloat(text4, "alpha", 1f))
        anim4.duration = duration
        anim4.interpolator = interpolator
        anim4.startDelay = delay * 3
        val set = AnimatorSet()
        set.play(anim1).with(anim2)
        set.play(anim2).with(anim3)
        set.play(anim3).with(anim4)
        set.start()
    }
}
