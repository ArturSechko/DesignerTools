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
import com.digitex.designertools.designerApplication
import com.digitex.designertools.ext.setOnSeekBarChangeListener
import com.digitex.designertools.qs.OnOffTileState
import com.digitex.designertools.utils.Preferences
import com.digitex.designertools.utils.cancelGridOverlayOrUnpublishTile
import com.digitex.designertools.utils.lauchGridOverlayOrPublishTile
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
            Preferences.Grid.setGridColumnSize(size)
        } else if (seekBar === rowSeekBar) {
            gridPreview.rowSizeDp = size
            Preferences.Grid.setGridRowSize(size)
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

        columnSeekBar.progress = (Preferences.Grid.getGridColumnSize(8) - 4) / 2
        rowSeekBar.progress = (Preferences.Grid.getGridRowSize(8) - 4) / 2
        gridPreview.columnSizeDp = Preferences.Grid.getGridColumnSize(8)
        gridPreview.rowSizeDp = Preferences.Grid.getGridRowSize(8)

        columnSeekBar.setOnSeekBarChangeListener(onProgressChanged = seekBarChangeAction)
        rowSeekBar.setOnSeekBarChangeListener(onProgressChanged = seekBarChangeAction)

        keylinesCheckBox.isChecked = Preferences.Grid.getShowKeylines()
        keylinesCheckBox.setOnCheckedChangeListener { _, isChecked ->
            Preferences.Grid.setShowKeylines(isChecked)
        }

        setIncludeCustomGridLines(Preferences.Grid.getUseCustomGridSize())
        customGridSizeCheckBox.setOnCheckedChangeListener { _, isChecked ->
            Preferences.Grid.setUseCustomGridSize(isChecked)
            if (isChecked) {
                Preferences.Grid.setGridColumnSize(gridPreview.columnSizeDp)
                Preferences.Grid.setGridRowSize(gridPreview.rowSizeDp)
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
        Preferences.prefs.registerOnSharedPreferenceChangeListener(this)
        enableSwitch.isChecked = designerApplication.isGridOverlayOn
    }

    override fun onPause() {
        super.onPause()
        Preferences.prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked == designerApplication.isGridOverlayOn) return
        if (isChecked) {
            lauchGridOverlayOrPublishTile(
                    if (Preferences.Grid.getGridOverlayActive())
                        OnOffTileState.STATE_ON
                    else
                        OnOffTileState.STATE_OFF
            )
        } else {
            cancelGridOverlayOrUnpublishTile()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (Preferences.Grid.gridLineColor == key) {
            colorPicker.primaryColor = Preferences.Grid.getGridLineColor()
        } else if (Preferences.Grid.keylineColor == key) {
            colorPicker.secondaryColor = Preferences.Grid.getKeylineColor()
        }
    }

    private fun setIncludeCustomGridLines(include: Boolean) {
        customGridSizeCheckBox.isChecked = include
        columnSeekBar.isEnabled = include
        rowSeekBar.isEnabled = include
    }
}
