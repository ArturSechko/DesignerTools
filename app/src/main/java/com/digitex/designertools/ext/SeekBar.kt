package com.digitex.designertools.ext

import android.widget.SeekBar

internal inline fun SeekBar.doOnProgressChanged(
        crossinline action: (
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
        ) -> Unit
) = setOnSeekBarChangeListener(onProgressChanged = action)

internal inline fun SeekBar.doOnStartTrackingTouch(
        crossinline action: (seekBar: SeekBar) -> Unit
) = setOnSeekBarChangeListener(onStartTrackingTouch = action)

internal inline fun SeekBar.doOnStopTrackingTouch(
        crossinline action: (seekBar: SeekBar) -> Unit
) = setOnSeekBarChangeListener(onStopTrackingTouch = action)

internal inline fun SeekBar.setOnSeekBarChangeListener(
        crossinline onProgressChanged: (
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
        ) -> Unit = { _, _, _ -> },
        crossinline onStartTrackingTouch: (
                seekBar: SeekBar
        ) -> Unit = { _ -> },
        crossinline onStopTrackingTouch: (
                seekBar: SeekBar
        ) -> Unit = { _ -> }
): SeekBar.OnSeekBarChangeListener {
    val listener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) = onProgressChanged(seekBar, progress, fromUser)
        override fun onStartTrackingTouch(seekBar: SeekBar) = onStartTrackingTouch(seekBar)
        override fun onStopTrackingTouch(seekBar: SeekBar) = onStopTrackingTouch(seekBar)
    }

    setOnSeekBarChangeListener(listener)

    return listener
}