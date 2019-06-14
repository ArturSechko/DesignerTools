package com.digitex.designertools.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import com.digitex.designertools.R
import com.digitex.designertools.service.ScreenshotListenerService
import com.digitex.designertools.utils.Preferences
import kotlinx.android.synthetic.main.card_header.*

class ScreenshotCardFragment : DesignerToolCardFragment(R.layout.card_layout_screenshot) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cardTitle.setText(R.string.header_title_screenshot)
        cardSummary.setText(R.string.header_summary_screenshot)
        cardIcon.setImageResource(R.drawable.ic_qs_screenshotinfo_on)
        enableSwitch.isChecked = Preferences.Screenshot.getScreenshotInfoEnabled()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Preferences.Screenshot.setScreenshotInfoEnabled(true)
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
                Preferences.Screenshot.setScreenshotInfoEnabled(isChecked)
                val newIntent = Intent(context, ScreenshotListenerService::class.java)
                requireContext().startService(newIntent)
            } else {
                enableSwitch.isChecked = false
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 42)
            }
        } else {
            Preferences.Screenshot.setScreenshotInfoEnabled(false)
        }
    }
}
