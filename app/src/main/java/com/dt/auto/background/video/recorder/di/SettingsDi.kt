package com.dt.auto.background.video.recorder.di

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleService
import com.dt.auto.background.video.recorder.application.RecorderApplication
import com.dt.auto.background.video.recorder.app_ui.activity.settings.use_case.Settings


val RecorderApplication.settings
    get() = Settings.getInstance(applicationContext)


val AppCompatActivity.settings
    get() = Settings.getInstance(this)

val LifecycleService.settings
    get() = Settings.getInstance(this)


val Fragment.settings
    get() = Settings.getInstance(requireContext())

