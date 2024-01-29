package com.dt.auto.background.video.recorder.ads_integration.admob

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log
import androidx.annotation.Keep
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.appizona.yehiahd.fastsave.FastSave
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases
import com.dt.auto.background.video.recorder.application.RecorderApplication
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.isPurchase
import com.dt.auto.background.video.recorder.domain.remote.HelperUtilsRemoteConfig.Companion.appOpenAdId
import com.dt.auto.background.video.recorder.helpers.NetworkHelper
import com.dt.auto.background.video.recorder.helpers.utils.Constants
import java.util.*

@Keep
class AppOpenManager(private val myApplication: RecorderApplication) : ActivityLifecycleCallbacks,
        LifecycleObserver {
    private var appOpenAd: AppOpenAd? = null
    private var currentActivity: Activity? = null
    private var loadTime: Long = 0
    private var loadCallback: AppOpenAdLoadCallback? = null

    private var listener:Listener?=null

    /**
     * LifecycleObserver methods
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        if (!isPurchase) {
            showAdIfAvailable()
        }

    }

    interface Listener {
        fun onNativeAdClosed()
    }
    /**
     * Request an ad
     */
    fun fetchAd() {
        // Have unused ad, no need to fetch another.
        if (isAdAvailable) {
            return
        }
        loadCallback = object : AppOpenAdLoadCallback() {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                super.onAdLoaded(appOpenAd)
                this@AppOpenManager.appOpenAd = appOpenAd
                loadTime = Date().time
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                isShowingAd = false
            }
        }
        val request = adRequest
        try {
            if (!isPurchase) {
                AppOpenAd.load(
                    myApplication, "ca-app-pub-7398630592274943/1926507688", request,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback!!
                )
            }
        } catch (e: Exception) {
        }
    }

    /**
     * Creates and returns ad request.
     */
    private val adRequest: AdRequest get() = AdRequest.Builder().build()

    /**
     * Utility method to check if ad was loaded more than n hours ago.
     */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    val isAdAvailable: Boolean
        get() = appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)

    /**
     * ActivityLifecycleCallback methods
     */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    /**
     * Shows the ad if one isn't already showing.
     */
    fun showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable) {
//            Log.d(LOG_TAG, "Will show ad.");
            val fullScreenContentCallback: FullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            appOpenAd = null
                            isShowingAd = false
                            listener?.onNativeAdClosed()
                            try {
                                fetchAd()
                            } catch (e: Exception) {
                                Log.i("MyKey", "onAdDismissedFullScreenContent: " + e.localizedMessage)
                            }
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {}
                        override fun onAdShowedFullScreenContent() {
                            isShowingAd = true
                        }
                    }
            appOpenAd?.fullScreenContentCallback = fullScreenContentCallback
            if (FastSave.getInstance().getBoolean("show_app_open", false)) {
                valueforsplash = false
                isShowingAd=false

                FastSave.getInstance().saveBoolean("show_app_open", valueforsplash)
                Log.d(LOG_TAG, "value is true.. ad not show")
            } else currentActivity?.let {
                isShowingAd=true
                appOpenAd?.show(it)
            }
            Log.d(LOG_TAG, "Will show ad.")
        } else {
            Log.d(LOG_TAG, "Can not show ad. else")
            try {
                fetchAd()
            } catch (e: Exception) {
                Log.i("MyKey", "showAdIfAvailable: " + e.localizedMessage)
            }
        }
    }

    companion object {
        private const val LOG_TAG = "AppOpenManager"
        var isShowingAd = false
        var valueforsplash = false
    }

    /**
     * Constructor
     */
    init {
        myApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        listener = myApplication as? Listener
    }
}