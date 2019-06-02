package com.digitex.designertools.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.SeekBar
import com.digitex.designertools.R
import com.digitex.designertools.ext.setOnSeekBarChangeListener
import com.digitex.designertools.qs.OnOffTileState
import com.digitex.designertools.utils.ColorUtils
import com.digitex.designertools.utils.LaunchUtils
import com.digitex.designertools.utils.PreferenceUtils
import com.digitex.designertools.utils.PreferenceUtils.GridPreferences
import kotlinx.android.synthetic.main.card_header.*
import kotlinx.android.synthetic.main.card_layout.view.*
import kotlinx.android.synthetic.main.include_grid_overlay.*

class GridOverlayCardFragment : DesignerToolCardFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    override val cardStyleResId: Int get() = R.style.AppTheme_GridOverlayCard
    override val backgroundTint: Int get() = R.color.colorGridOverlayCardTint

    private val seekBarChangeAction: (
            seekBar: SeekBar,
            progress: Int,
            fromUser: Boolean
    ) -> Unit = { seekBar, progress, _ ->
        val size = 4 + progress * 2
        if (seekBar === columnSeekBar) {
            gridPreview.columnSizeDp = size
            GridPreferences.setGridColumnSize(context, size)
        } else if (seekBar === rowSeekBar) {
            gridPreview.rowSizeDp = size
            GridPreferences.setGridRowSize(context, size)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = super.onCreateView(inflater, container, savedInstanceState)?.apply {
        inflater.inflate(R.layout.include_grid_overlay, this.cardContent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cardTitle.setText(R.string.header_title_grid_overlay)
        cardSummary.setText(R.string.header_summary_grid_overlay)
        cardIcon.setImageResource(R.drawable.ic_qs_grid_on)

        columnSeekBar.progress = (GridPreferences.getGridColumnSize(context, 8) - 4) / 2
        rowSeekBar.progress = (GridPreferences.getGridRowSize(context, 8) - 4) / 2
        gridPreview.columnSizeDp = GridPreferences.getGridColumnSize(context, 8)
        gridPreview.rowSizeDp = GridPreferences.getGridRowSize(context, 8)

        columnSeekBar.setOnSeekBarChangeListener(onProgressChanged = seekBarChangeAction)
        rowSeekBar.setOnSeekBarChangeListener(onProgressChanged = seekBarChangeAction)

        keylinesCheckBox.isChecked = GridPreferences.getShowKeylines(context, false)
        keylinesCheckBox.setOnCheckedChangeListener { _, isChecked ->
            GridPreferences.setShowKeylines(context, isChecked)
        }

        setIncludeCustomGridLines(GridPreferences.getUseCustomGridSize(context, false))
        customGridSizeCheckBox.setOnCheckedChangeListener { _, isChecked ->
            GridPreferences.setUseCustomGridSize(context, isChecked)
            if (isChecked) {
                GridPreferences.setGridColumnSize(context, gridPreview.columnSizeDp)
                GridPreferences.setGridRowSize(context, gridPreview.rowSizeDp)
            }
            columnSeekBar.isEnabled = isChecked
            rowSeekBar.isEnabled = isChecked
        }

        rowSeekBar.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> view.parent.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
            }
            view.onTouchEvent(event)
            true
        }

        colorPicker.setOnClickListener {
            DualColorPickerDialog().show(fragmentManager!!, "color_picker_dialog")
        }
    }

    override fun onResume() {
        super.onResume()
        PreferenceUtils.getShardedPreferences(context)
                .registerOnSharedPreferenceChangeListener(this)
        enableSwitch.isChecked = applicationContext.isGridOverlayOn
    }

    override fun onPause() {
        super.onPause()
        PreferenceUtils.getShardedPreferences(context)
                .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked == applicationContext.isGridOverlayOn) return
        if (isChecked) {
            LaunchUtils.lauchGridOverlayOrPublishTile(
                    context,
                    if (GridPreferences.getGridOverlayActive(context, false))
                        OnOffTileState.STATE_ON
                    else
                        OnOffTileState.STATE_OFF
            )
        } else {
            LaunchUtils.cancelGridOverlayOrUnpublishTile(context)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (GridPreferences.KEY_GRID_LINE_COLOR == key) {
            colorPicker.primaryColor = ColorUtils.getGridLineColor(context)
        } else if (GridPreferences.KEY_KEYLINE_COLOR == key) {
            colorPicker.secondaryColor = ColorUtils.getKeylineColor(context)
        }
    }

    private fun setIncludeCustomGridLines(include: Boolean) {
        customGridSizeCheckBox.isChecked = include
        columnSeekBar.isEnabled = include
        rowSeekBar.isEnabled = include
    }
}
