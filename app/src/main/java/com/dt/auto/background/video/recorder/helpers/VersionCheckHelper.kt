package com.dt.auto.background.video.recorder.helpers

import android.os.Build

object VersionCheckHelper {

    fun isHoneyComb() = Build.VERSION.SDK_INT >= 13
    fun isMarshmallowPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    fun isNougatPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    fun isNougatMR1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
    fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    fun isOreoMr1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
    fun isPiePlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    fun isQPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    fun isRPlus() = Build.VERSION.SDK_INT >= 30
    fun isLollipopPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP


}