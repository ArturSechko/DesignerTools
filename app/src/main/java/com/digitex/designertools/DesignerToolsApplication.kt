package com.digitex.designertools

import android.app.Activity
import android.app.Application
import android.content.Intent
import com.digitex.designertools.utils.Preferences

internal val designerApplication: DesignerToolsApplication = DesignerToolsApplication.instance

class DesignerToolsApplication : Application() {

    var screenRecordResultCode = Activity.RESULT_CANCELED
        private set
    var screenRecordResultData: Intent = Intent()
        private set

    var isGridOverlayOn: Boolean = false
        get() = field || Preferences.Grid.getGridQsTileEnabled()
    var isMockOverlayOn: Boolean = false
        get() = field || Preferences.Mock.getMockQsTileEnabled()
    var isColorPickerOn: Boolean = false
        get() = field || Preferences.ColorPicker.getColorPickerQsTileEnabled()

    init {
        instance = this
    }

    fun setScreenRecordPermissionData(resultCode: Int, resultData: Intent?) {
        screenRecordResultCode = resultCode
        screenRecordResultData = resultData ?: Intent()
    }

    companion object {
        lateinit var instance: DesignerToolsApplication
    }
}
