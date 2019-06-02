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
import com.digitex.designertools.qs.OnOffTileState
import com.digitex.designertools.utils.ImageUtils
import com.digitex.designertools.utils.LaunchUtils
import com.digitex.designertools.utils.MockupUtils
import com.digitex.designertools.utils.PreferenceUtils.MockPreferences
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

        portraitImage.setImageBitmap(MockupUtils.getPortraitMockup(context))
        portraitImage.setOnClickListener {
            startActivityForResult(
                    Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" },
                    REQUEST_PICK_PORTRAIT_IMAGE
            )
        }
        landscapeImage.setImageBitmap(MockupUtils.getLandscapeMockup(context))
        landscapeImage.setOnClickListener {
            startActivityForResult(
                    Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" },
                    REQUEST_PICK_LANDSCAPE_IMAGE
            )
        }
        resetBtn.setOnClickListener {
            try {
                MockupUtils.savePortraitMockup(context, null)
                portraitImage.setImageBitmap(null)
                MockupUtils.saveLandscapeMockup(context, null)
                landscapeImage.setImageBitmap(null)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        opacitySeekBar.doOnProgressChanged { _, progress, _ ->
            val opacity = (progress + 1) * 10
            MockPreferences.setMockOpacity(context, opacity)
            setOpacityLevel(opacity)
        }
        val opacity = MockPreferences.getMockOpacity(context, 10)
        setOpacityLevel(opacity)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked == designerApplication.isMockOverlayOn) return
        if (isChecked) {
            LaunchUtils.lauchMockPverlayOrPublishTile(
                    context,
                    if (MockPreferences.getMockOverlayActive(context, false))
                        OnOffTileState.STATE_ON
                    else
                        OnOffTileState.STATE_OFF
            )
        } else {
            LaunchUtils.cancelMockOverlayOrUnpublishTile(context)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PICK_PORTRAIT_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                val selectedImage = data!!.data
                if (selectedImage != null) {
                    val overlay = ImageUtils.getBitmapFromUri(context, selectedImage)
                    try {
                        MockupUtils.savePortraitMockup(context, overlay)
                        portraitImage.setImageBitmap(overlay)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
            REQUEST_PICK_LANDSCAPE_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                val selectedImage = data!!.data
                if (selectedImage != null) {
                    val overlay = ImageUtils.getBitmapFromUri(context, selectedImage)
                    try {
                        MockupUtils.saveLandscapeMockup(context, overlay)
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
