package com.digitex.designertools.service

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.content.getSystemService
import androidx.core.graphics.createBitmap
import androidx.core.view.drawToBitmap
import com.digitex.designertools.R
import com.digitex.designertools.utils.isCyanogenMod
import kotlinx.android.synthetic.main.screenshot_info.view.*
import java.io.*
import java.lang.reflect.InvocationTargetException
import java.text.DateFormat
import java.util.*
import java.util.regex.Pattern

class ScreenshotInfoService : IntentService(ScreenshotInfoService::class.java.simpleName) {

    companion object {
        private val TAG = ScreenshotInfoService::class.java.simpleName
        private const val FILENAME_PROC_VERSION = "/proc/version"

        const val EXTRA_PATH = "path"
    }

    override fun onHandleIntent(intent: Intent) {
        if (intent.hasExtra(EXTRA_PATH)) {
            val screenshot = File(intent.getStringExtra(EXTRA_PATH))
            val paneBmp = getInfoPane(screenshot)
            val screenshotBmp = BitmapFactory.decodeFile(screenshot.absolutePath)
            try {
                saveModifiedScreenshot(screenshotBmp, paneBmp, screenshot.absolutePath)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private fun getInfoPane(screenshot: File): Bitmap {
        val date = Date(screenshot.lastModified())
        val dateTime = String.format(
                "%s at %s",
                DateFormat.getDateInstance().format(date),
                DateFormat.getTimeInstance().format(date)
        )
        val build = getCmVersionString(this)
        val density = getDensityString()
        val kernelVersion = getFormattedKernelVersion()

        val pane = View.inflate(this, R.layout.screenshot_info, null)
        pane.date_time_info.text = dateTime
        pane.device_name.text = Build.MODEL
        pane.code_name.text = Build.DEVICE
        pane.build.text = build
        pane.density.text = density
        pane.kernel.text = kernelVersion

        return pane.renderToBitmap()
    }

    @SuppressLint("PrivateApi")
    private fun getCmVersionString(context: Context): String {
        if (isCyanogenMod()) {
            val cl = context.classLoader
            val systemProperties: Class<*>?
            try {
                systemProperties = cl.loadClass("android.os.SystemProperties")
                //Parameters Types
                val paramTypes = arrayOfNulls<Class<*>>(1)
                paramTypes[0] = String::class.java

                val get = systemProperties!!.getMethod("get", *paramTypes)

                //Parameters
                val params = arrayOfNulls<Any>(1)
                params[0] = "ro.cm.version"

                return get.invoke(systemProperties, *params) as String
            } catch (e: ClassNotFoundException) {
            } catch (e: NoSuchMethodException) {
            } catch (e: IllegalAccessException) {
            } catch (e: InvocationTargetException) {
                /* don't care, will fallback to Build.ID */
            }
        }

        return Build.ID
    }

    private fun getDensityString(): String {
        val wm = getSystemService<WindowManager>()!!
        val display = wm.defaultDisplay
        val size = Point()
        display.getRealSize(size)

        val dm = resources.displayMetrics
        var string = ""
        string += when (dm.densityDpi) {
            DisplayMetrics.DENSITY_MEDIUM -> "mdpi"
            DisplayMetrics.DENSITY_HIGH -> "hdpi"
            DisplayMetrics.DENSITY_XHIGH -> "xhdpi"
            DisplayMetrics.DENSITY_XXHIGH -> "xxhdpi"
            DisplayMetrics.DENSITY_XXXHIGH -> "xxxhdpi"
            else -> dm.densityDpi.toString() + "dpi"
        }
        string += "(${dm.density}x) - ${size.x}x${size.y}"
        return string
    }

    @Throws(FileNotFoundException::class)
    private fun saveModifiedScreenshot(
            screenshot: Bitmap,
            infoPane: Bitmap,
            filePath: String
    ) {
        val wm = getSystemService<WindowManager>()!!
        val size = Point()
        wm.defaultDisplay.getRealSize(size)
        if (screenshot.width != size.x || screenshot.height != size.y) {
            Log.d(TAG, "Not adding info, screenshot too large")
            return
        }

        val newBitmap = createBitmap(
                screenshot.width + infoPane.width,
                screenshot.height
        )
        val canvas = Canvas(newBitmap)
        canvas.drawColor(getColor(R.color.screenshot_info_background_color))
        canvas.drawBitmap(screenshot, 0f, 0f, null)
        canvas.drawBitmap(infoPane, screenshot.width.toFloat(), 0f, null)
        screenshot.recycle()
        infoPane.recycle()
        newBitmap.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(filePath))
        newBitmap.recycle()
    }

    @Throws(IOException::class)
    private fun readLine(filename: String): String {
        BufferedReader(FileReader(filename), 256).use { reader ->
            return reader.readLine()
        }
    }

    private fun formatKernelVersion(rawKernelVersion: String): String {
        // Example (see tests for more):
        // Linux version 3.0.31-g6fb96c9 (android-build@xxx.xxx.xxx.xxx.com) \
        //     (gcc version 4.6.x-xxx 20120106 (prerelease) (GCC) ) #1 SMP PREEMPT \
        //     Thu Jun 28 11:02:39 PDT 2012

        val procVersionRegex = "Linux version (\\S+) " + /* group 1: "3.0.31-g6fb96c9" */
                "\\((\\S+?)\\) " +        /* group 2: "x@y.com" (kernel builder) */
                "(?:\\(gcc.+? \\)) " +    /* ignore: GCC version information */
                "(#\\d+) " +              /* group 3: "#1" */
                "(?:.*?)?" +              /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
                "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)" /* group 4: "Thu Jun 28 11:02:39 PDT 2012" */

        val m = Pattern.compile(procVersionRegex).matcher(rawKernelVersion)
        if (!m.matches()) {
            Log.e(TAG, "Regex did not match on /proc/version: $rawKernelVersion")
            return "Unavailable"
        } else if (m.groupCount() < 4) {
            Log.e(TAG, "Regex match on /proc/version only returned " + m.groupCount()
                    + " groups")
            return "Unavailable"
        }
        return m.group(1) + "\n" +                 // 3.0.31-g6fb96c9
                m.group(2) + " " + m.group(3) + "\n" + // x@y.com #1
                m.group(4)                            // Thu Jun 28 11:02:39 PDT 2012
    }

    private fun getFormattedKernelVersion(): String {
        return try {
            formatKernelVersion(readLine(FILENAME_PROC_VERSION))
        } catch (e: IOException) {
            Log.e(TAG, "IO Exception when getting kernel version for Device Info screen", e)
            "Unavailable"
        }
    }
}

private fun View.renderToBitmap(): Bitmap {
    layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
    )
    measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    layout(0, 0, measuredWidth, measuredHeight)

    return drawToBitmap()
}