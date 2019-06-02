package com.digitex.designertools.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import androidx.core.content.getSystemService
import com.digitex.designertools.designerApplication

fun createNotificationChannel(channelId: String, channelName: String): String {
    return if (isAtLeastSdk(Build.VERSION_CODES.O)) {
        val service = designerApplication.getSystemService<NotificationManager>()!!
        service.createNotificationChannel(NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_NONE
        ).also {
            it.lightColor = Color.BLUE
            it.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        })
        channelId
    } else ""
}