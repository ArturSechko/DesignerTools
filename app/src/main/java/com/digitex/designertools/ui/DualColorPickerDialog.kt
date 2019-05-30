package com.digitex.designertools.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.PagerAdapter
import com.digitex.designertools.R
import com.digitex.designertools.utils.ColorUtils
import com.digitex.designertools.utils.PreferenceUtils.GridPreferences
import kotlinx.android.synthetic.main.dialog_color_picker.view.*
import kotlinx.android.synthetic.main.lobsterpicker.view.*

class DualColorPickerDialog : DialogFragment() {

    private var colorPickerViews: Array<ColorPickerViewHolder> = arrayOf(ColorPickerViewHolder(), ColorPickerViewHolder())

    private val sliderTouchListener = View.OnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> v.parent.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
        }
        v.onTouchEvent(event)
        true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(context, R.layout.dialog_color_picker, null).apply {
            viewPager.adapter = ColorPickerPagerAdapter()

            viewPagerIndicator.setViewPager(viewPager)
            viewPagerIndicator.fillColor = requireContext().getColor(R.color.colorGridOverlayCardTint)
        }

        initColorPickerViews()

        return AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppDialog))
                .setView(view)
                .setTitle(R.string.color_picker_title)
                .setPositiveButton(R.string.color_picker_accept) { _, _ ->
                    GridPreferences.setGridLineColor(context, colorPickerViews[0].container.colorPicker.color)
                    GridPreferences.setKeylineColor(context, colorPickerViews[1].container.colorPicker.color)
                }
                .setNegativeButton(R.string.color_picker_cancel) { dialog, _ -> dialog.dismiss() }
                .create()
    }

    private fun initColorPickerViews() {
        colorPickerViews[0].container = View.inflate(context, R.layout.lobsterpicker, null)
        with(colorPickerViews[0].container) {
            colorPicker.addDecorator(opacitySeekBar)
            val color = ColorUtils.getGridLineColor(context)
            colorPicker.color = color
            colorPicker.history = color
            opacitySeekBar.setOnTouchListener(sliderTouchListener)
        }

        colorPickerViews[1].container = View.inflate(context, R.layout.lobsterpicker, null)
        with(colorPickerViews[1].container) {
            colorPicker.addDecorator(opacitySeekBar)
            val color = ColorUtils.getKeylineColor(context)
            colorPicker.color = color
            colorPicker.history = color
            opacitySeekBar.setOnTouchListener(sliderTouchListener)
        }
    }

    private inner class ColorPickerPagerAdapter : PagerAdapter() {

        override fun getCount(): Int = colorPickerViews.size

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view === `object`

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            container.addView(colorPickerViews[position].container)

            return colorPickerViews[position].container
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return requireContext().getString(
                    if (position == 0)
                        R.string.color_picker_grid_page_title
                    else
                        R.string.color_picker_keyline_page_title
            )
        }
    }

    private inner class ColorPickerViewHolder {
        internal lateinit var container: View
    }
}
