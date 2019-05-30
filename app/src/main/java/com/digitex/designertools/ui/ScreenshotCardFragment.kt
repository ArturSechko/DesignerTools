package com.digitex.designertools.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import com.digitex.designertools.R
import com.digitex.designertools.service.ScreenshotListenerService
import com.digitex.designertools.utils.PreferenceUtils.ScreenshotPreferences
import kotlinx.android.synthetic.main.card_header.*

class ScreenshotCardFragment : DesignerToolCardFragment() {

    override val cardStyleResId: Int get() = R.style.AppTheme_ScreenshotCard
    override val backgroundTint: Int get() = R.color.colorScreenshotCardTint

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cardTitle.setText(R.string.header_title_screenshot)
        cardSummary.setText(R.string.header_summary_screenshot)
        cardIcon.setImageResource(R.drawable.ic_qs_screenshotinfo_on)
        enableSwitch.isChecked = ScreenshotPreferences.getScreenshotInfoEnabled(context, false)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ScreenshotPreferences.setScreenshotInfoEnabled(context, true)
            val newIntent = Intent(context, ScreenshotListenerService::class.java)
            requireContext().startService(newIntent)
            enableSwitch.isChecked = true
        } else {
            enableSwitch.isChecked = false
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (!isVisible) return

        if (isChecked) {
            if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                ScreenshotPreferences.setScreenshotInfoEnabled(context, isChecked)
                val newIntent = Intent(context, ScreenshotListenerService::class.java)
                requireContext().startService(newIntent)
            } else {
                enableSwitch.isChecked = false
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 42)
            }
        } else {
            ScreenshotPreferences.setScreenshotInfoEnabled(context, false)
        }
    }
}
