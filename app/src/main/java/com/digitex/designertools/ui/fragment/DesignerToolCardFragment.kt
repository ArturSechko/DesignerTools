package com.digitex.designertools.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.card_header.view.*

open class DesignerToolCardFragment(
        @LayoutRes layoutResId: Int
) : Fragment(layoutResId), CompoundButton.OnCheckedChangeListener {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = super.onCreateView(inflater, container, savedInstanceState)?.apply {
        enableSwitch.setOnCheckedChangeListener(this@DesignerToolCardFragment)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {}
}
