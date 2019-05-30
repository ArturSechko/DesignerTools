package com.digitex.designertools.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.CompoundButton
import com.digitex.designertools.R
import com.digitex.designertools.qs.OnOffTileState
import com.digitex.designertools.utils.LaunchUtils
import com.digitex.designertools.utils.PreferenceUtils.ColorPickerPreferences
import kotlinx.android.synthetic.main.card_header.*

class ColorPickerCardFragment : DesignerToolCardFragment() {

    override val cardStyleResId: Int get() = R.style.AppTheme_ColorPickerCard
    override val backgroundTint: Int get() = R.color.colorColorPickerCardTint

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cardTitle.setText(R.string.header_title_color_picker)
        cardSummary.setText(R.string.header_summary_color_picker)
        cardIcon.setImageResource(R.drawable.ic_qs_colorpicker_on)
    }

    override fun onResume() {
        super.onResume()
        enableSwitch.isChecked = applicationContext.colorPickerOn
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked == applicationContext.colorPickerOn) return
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
            LaunchUtils.lauchColorPickerOrPublishTile(
                    context,
                    if (ColorPickerPreferences.getColorPickerActive(context, false))
                        OnOffTileState.STATE_ON
                    else
                        OnOffTileState.STATE_OFF
            )
        } else {
            LaunchUtils.cancelColorPickerOrUnpublishTile(context)
        }
    }

    companion object {
        private const val REQUEST_OVERLAY_PERMISSION = 0x42
    }
}
