package com.digitex.designertools.ui

import android.content.ContextWrapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import com.digitex.designertools.DesignerToolsApplication
import com.digitex.designertools.R
import kotlinx.android.synthetic.main.card_header.view.*

open class DesignerToolCardFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    protected open val cardStyleResId: Int get() = R.style.AppTheme
    protected open val backgroundTint: Int get() = android.R.color.transparent

    protected val applicationContext: DesignerToolsApplication
        get() = requireActivity().applicationContext as DesignerToolsApplication

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val context = ContextWrapper(inflater.context)
        context.setTheme(cardStyleResId)
        inflater.cloneInContext(context)
        return inflater.inflate(R.layout.card_layout, container, true).apply {
            backgroundTintList = getContext().getColorStateList(backgroundTint)
            enableSwitch.setOnCheckedChangeListener(this@DesignerToolCardFragment)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {}
}
