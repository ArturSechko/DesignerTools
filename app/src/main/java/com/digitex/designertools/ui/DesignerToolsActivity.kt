package com.digitex.designertools.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.digitex.designertools.R
import com.digitex.designertools.utils.isCyanogenMod
import kotlinx.android.synthetic.main.activity_designer_tools.*

class DesignerToolsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_designer_tools)
        if (!isCyanogenMod()) {
            qsTilesTextView.setText(R.string.overlays_section_text)
        }
    }
}
