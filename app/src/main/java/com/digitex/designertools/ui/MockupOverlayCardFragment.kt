package com.digitex.designertools.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.digitex.designertools.R
import com.digitex.designertools.designerApplication
import com.digitex.designertools.ext.doOnProgressChanged
import com.digitex.designertools.ext.getBitmap
import com.digitex.designertools.qs.OnOffTileState
import com.digitex.designertools.utils.*
import kotlinx.android.synthetic.main.card_header.*
import kotlinx.android.synthetic.main.card_layout.view.*
import kotlinx.android.synthetic.main.include_mockup_overlay.*
import java.io.IOException

class MockupOverlayCardFragment : DesignerToolCardFragment() {

    override val cardStyleResId: Int get() = R.style.AppTheme_MockupOverlayCard
    override val backgroundTint: Int get() = R.color.colorMockupOverlayCardTint

    override fun onResume() {
        super.onResume()
        enableSwitch.isChecked = designerApplication.isMockOverlayOn
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = super.onCreateView(inflater, container, savedInstanceState)?.apply {
        inflater.inflate(R.layout.include_mockup_overlay, this.cardContent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cardTitle.setText(R.string.header_title_mockup_overlay)
        cardSummary.setText(R.string.header_summary_mockup_overlay)
        cardIcon.setImageResource(R.drawable.ic_qs_overlay_on)

        portraitImage.setImageBitmap(getPortraitMockup())
        portraitImage.setOnClickListener {
            startActivityForResult(
                    Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" },
                    REQUEST_PICK_PORTRAIT_IMAGE
            )
        }
        landscapeImage.setImageBitmap(getLandscapeMockup())
        landscapeImage.setOnClickListener {
            startActivityForResult(
                    Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" },
                    REQUEST_PICK_LANDSCAPE_IMAGE
            )
        }
        resetBtn.setOnClickListener {
            try {
                savePortraitMockup(null)
                portraitImage.setImageBitmap(null)
                saveLandscapeMockup(null)
                landscapeImage.setImageBitmap(null)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        opacitySeekBar.doOnProgressChanged { _, progress, _ ->
            val opacity = (progress + 1) * 10
            Preferences.Mock.setMockOpacity(opacity)
            setOpacityLevel(opacity)
        }
        val opacity = Preferences.Mock.getMockOpacity(10)
        setOpacityLevel(opacity)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked == designerApplication.isMockOverlayOn) return
        if (isChecked) {
            launchMockOverlayOrPublishTile(
                    if (Preferences.Mock.getMockOverlayActive())
                        OnOffTileState.STATE_ON
                    else
                        OnOffTileState.STATE_OFF
            )
        } else {
            cancelMockOverlayOrUnpublishTile()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PICK_PORTRAIT_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                val selectedImage = data!!.data
                if (selectedImage != null) {
                    val overlay = selectedImage.getBitmap()
                    try {
                        savePortraitMockup(overlay)
                        portraitImage.setImageBitmap(overlay)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
            REQUEST_PICK_LANDSCAPE_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                val selectedImage = data!!.data
                if (selectedImage != null) {
                    val overlay = selectedImage.getBitmap()
                    try {
                        saveLandscapeMockup(overlay)
                        landscapeImage.setImageBitmap(overlay)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun setOpacityLevel(opacityLevel: Int) {
        opacityTv.text = requireContext().getString(R.string.opacity_format, opacityLevel)
        opacitySeekBar.progress = opacityLevel / 10 - 1
    }

    companion object {
        private const val REQUEST_PICK_PORTRAIT_IMAGE = 1000
        private const val REQUEST_PICK_LANDSCAPE_IMAGE = 1001
    }
}
