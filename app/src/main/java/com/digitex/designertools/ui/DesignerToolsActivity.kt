package com.digitex.designertools.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.digitex.designertools.R
import com.digitex.designertools.utils.LaunchUtils
import kotlinx.android.synthetic.main.activity_designer_tools.*
import kotlinx.android.synthetic.main.credits_header.*

class DesignerToolsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_designer_tools)
        if (!LaunchUtils.isCyanogenMod(this)) {
            qsTilesTextView.setText(R.string.overlays_section_text)
        }
        headerImage.setOnClickListener {
            startActivity(Intent(this@DesignerToolsActivity, CreditsActivity::class.java))
        }
    }
}
