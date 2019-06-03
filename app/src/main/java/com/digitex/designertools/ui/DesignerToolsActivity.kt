package com.digitex.designertools.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import com.digitex.designertools.R
import com.digitex.designertools.ext.doOnApplyWindowInsets
import com.digitex.designertools.utils.isCyanogenMod
import kotlinx.android.synthetic.main.activity_designer_tools.*

class DesignerToolsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_designer_tools)
        if (!isCyanogenMod()) {
            qsTilesTextView.setText(R.string.overlays_section_text)
        }

        rootView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        contentView.doOnApplyWindowInsets { view, insets, padding ->
            view.updatePadding(
                    left = padding.left + insets.systemWindowInsetLeft,
                    top = padding.top + insets.systemWindowInsetTop,
                    right = padding.right + insets.systemWindowInsetRight,
                    bottom = padding.bottom + insets.systemWindowInsetBottom
            )
        }
    }
}
