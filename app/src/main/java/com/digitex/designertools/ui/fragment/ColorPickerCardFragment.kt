package com.digitex.designertools.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.CompoundButton
import com.digitex.designertools.R
import com.digitex.designertools.designerApplication
import com.digitex.designertools.qs.OnOffTileState
import com.digitex.designertools.utils.Preferences
import com.digitex.designertools.utils.cancelColorPickerOrUnpublishTile
import com.digitex.designertools.utils.launchColorPickerOrPublishTile
import kotlinx.android.synthetic.main.card_header.*

class ColorPickerCardFragment : DesignerToolCardFragment(R.layout.card_layout_colorpicker) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cardTitle.setText(R.string.header_title_color_picker)
        cardSummary.setText(R.string.header_summary_color_picker)
        cardIcon.setImageResource(R.drawable.ic_qs_colorpicker_on)
    }

    override fun onResume() {
        super.onResume()
        enableSwitch.isChecked = designerApplication.isColorPickerOn
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked == designerApplication.isColorPickerOn) return
        enableFeature(isChecked)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            enableSwitch.isChecked = Settings.canDrawOverlays(context)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun enableFeature(enable: Boolean) {
        if (enable) {
            launchColorPickerOrPublishTile(
                    if (Preferences.ColorPicker.getColorPickerActive())
                        OnOffTileState.STATE_ON
                    else
                        OnOffTileState.STATE_OFF
            )
        } else {
            cancelColorPickerOrUnpublishTile()
        }
    }

    companion object {
        private const val REQUEST_OVERLAY_PERMISSION = 0x42
    }
}
