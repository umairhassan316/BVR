package com.dt.auto.background.video.recorder.application

import android.app.Application
import android.util.Log
import com.appizona.yehiahd.fastsave.FastSave
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.dt.auto.background.video.recorder.BuildConfig
import com.dt.auto.background.video.recorder.di.settings
import com.dt.auto.background.video.recorder.helpers.NetworkHelper.hasInternetConnection
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.isPurchase
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.setupBillingClient
import com.dt.auto.background.video.recorder.app_ui.activity.quality_selection.CAMERA_CHECK_FRONT_OR_BACK
import com.dt.auto.background.video.recorder.app_ui.activity.quality_selection.DEFAULT_QUALITY_IDX
import com.dt.auto.background.video.recorder.app_ui.activity.quality_selection.VideoQualitySelectionActivity.Companion.cameraIndex
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class RecorderApplication : Application() {

    companion object {
        @JvmStatic
        var flashMode = "FlashMode"


        @JvmField
        var mFirebaseAnalytics: FirebaseAnalytics? = null

        @JvmField
        var appLock = "AppLock"

        @JvmField
        var appPin = "AppPin"
    }

    override fun onCreate() {
        super.onCreate()

        if (hasInternetConnection(applicationContext)) {
            setupBillingClient(applicationContext)
        }

        FirebaseApp.initializeApp(applicationContext)
        FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.APPLICATION_ID)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)


        /*MobileAds.initialize(this) {}
        AudienceNetworkAds.initialize(this)*/
        FastSave.init(applicationContext)
//        setupRemoteConfiguration(applicationContext)



        /*
        * Values that we saved earlier using shared preferences
        * */
        cameraIndex = settings.isBackCameraInt
        CAMERA_CHECK_FRONT_OR_BACK = cameraIndex
        DEFAULT_QUALITY_IDX = settings.videoQualitySelected



        if (hasInternetConnection(this@RecorderApplication)) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("ffnet", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result
                Log.w("ffnet", "FCM Token: $token")

            })
        }

        if (hasInternetConnection(this) && !isPurchase) {
            //AppOpenManager(this)
        }

    }
}