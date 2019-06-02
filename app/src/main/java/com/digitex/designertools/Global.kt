package com.digitex.designertools

import android.os.Build

internal fun isAtLeastSdk(version: Int): Boolean = Build.VERSION.SDK_INT >= version