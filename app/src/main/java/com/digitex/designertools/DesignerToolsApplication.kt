package com.digitex.designertools

import android.app.Activity
import android.app.Application
import android.content.Intent
import com.digitex.designertools.utils.PreferenceUtils.*

internal val designerApplication: DesignerToolsApplication = DesignerToolsApplication.instance

class DesignerToolsApplication : Application() {

    var screenRecordResultCode = Activity.RESULT_CANCELED
        private set
    var screenRecordResultData: Intent? = null
        private set

    var isGridOverlayOn: Boolean = false
        get() = field || GridPreferences.getGridQsTileEnabled(this, false)
    var isMockOverlayOn: Boolean = false
        get() = field || MockPreferences.getMockQsTileEnabled(this, false)
    var isColorPickerOn: Boolean = false
        get() = field || ColorPickerPreferences.getColorPickerQsTileEnabled(this, false)

    init {
        instance = this
    }

    fun setScreenRecordPermissionData(resultCode: Int, resultData: Intent?) {
        screenRecordResultCode = resultCode
        screenRecordResultData = resultData
    }

    companion object {
        lateinit var instance: DesignerToolsApplication
    }
}
