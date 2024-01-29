package com.dt.auto.background.video.recorder.app_ui.activity.settings.use_case

import android.annotation.SuppressLint
import android.content.Context
import com.dt.auto.background.video.recorder.helpers.utils.unsafeLazy

class Settings(private val context: Context) {

    companion object {


        private const val SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_NAME"
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: Settings? = null

        fun getInstance(context: Context): Settings {
            return INSTANCE ?: Settings(context.applicationContext).apply { INSTANCE = this }
        }
    }

    private enum class Key {
        PERMISSION_CHECK,
        PRIVACY_POLICY_CHECK,
        RECORDING_DURATION,
        PREVIEW_SIZE,
        IS_BACK_CAMERA,
        HAS_PREVIEW,
        VIDEO_QUALITY_SELECTED,
        IS_PIN_ENABLED,
        PIN_LOCK,
        NICK_NAME,
        SCHEDULE_DURATION,
        SCHEDULE_TIME_HOURS,
        SCHEDULE_TIME_MIN,
        SCHEDULE_DATE,
        is_Purchased,
    }

    private val sharedPreferences by unsafeLazy {
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }


    var videoQualitySelected: Int
        get() = get(Key.VIDEO_QUALITY_SELECTED, 2)
        set(value) = set(Key.VIDEO_QUALITY_SELECTED, value)


    var recordingDuration: String?
        get() = get(Key.RECORDING_DURATION, "60")
        set(value) = set(Key.RECORDING_DURATION, value)

    var scheduleDuration: String?
        get() = get(Key.SCHEDULE_DURATION, "0")
        set(value) = set(Key.SCHEDULE_DURATION, value)

    var previewSize: String?
        get() = get(Key.PREVIEW_SIZE, "Medium")
        set(value) = set(Key.PREVIEW_SIZE, value)

    var hasPreview: Boolean
        get() = get(Key.HAS_PREVIEW, true)
        set(value) = set(Key.HAS_PREVIEW, value)


    var isBackCamera: String?
        get() = get(Key.IS_BACK_CAMERA, "Back Camera")
        set(value) = set(Key.IS_BACK_CAMERA, value)

    var isBackCameraInt: Int
        get() = get(Key.IS_BACK_CAMERA, 0) // back camera
        set(value) = set(Key.IS_BACK_CAMERA, value)

    var isFirstRunPerm: Boolean
        get() = get(Key.PERMISSION_CHECK, false)
        set(value) = set(Key.PERMISSION_CHECK, value)


    var isFirstRunMoveToPrivacyPolicy: Boolean
        get() = get(Key.PRIVACY_POLICY_CHECK, false)
        set(value) = set(Key.PRIVACY_POLICY_CHECK, value)

    var isPinEnabled: Boolean
        get() = get(Key.IS_PIN_ENABLED, false)
        set(value) = set(Key.IS_PIN_ENABLED, value)

    var isPinSet: String?
        get() = get(Key.PIN_LOCK, "") // back camera
        set(value) = set(Key.PIN_LOCK, value)

    var isNickname: String?
        get() = get(Key.NICK_NAME, "") // back camera
        set(value) = set(Key.NICK_NAME, value)

    var scheduletimeHr: Int
        get() = get(Key.SCHEDULE_TIME_HOURS, 0) // schedule time Hour
        set(value) = set(Key.SCHEDULE_TIME_HOURS, value)

    var scheduletimeMin: Int
        get() = get(Key.SCHEDULE_TIME_MIN, 0) // schedule time Min
        set(value) = set(Key.SCHEDULE_TIME_MIN, value)

    var scheduleDate: String?
        get() = get(Key.SCHEDULE_DATE, "") //schedule Date
        set(value) = set(Key.SCHEDULE_DATE, value)

    var is_Purchased: Boolean
        get() = get(Key.is_Purchased, false) //set local in app variable
        set(value) = set(Key.is_Purchased, value)

    private fun get(key: Key, default: String): String? {
        return sharedPreferences.getString(key.name, default)
    }

    private fun set(key: Key, value: String?) {
        return sharedPreferences.edit()
            .putString(key.name, value)
            .apply()
    }

    private fun get(key: Key, default: Int): Int {
        return sharedPreferences.getInt(key.name, default)
    }

    private fun set(key: Key, value: Int) {
        return sharedPreferences.edit()
            .putInt(key.name, value)
            .apply()
    }

    private fun get(key: Key, default: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key.name, default)
    }

    private fun set(key: Key, value: Boolean) {
        sharedPreferences.edit()
            .putBoolean(key.name, value)
            .apply()
    }


}