package com.dt.auto.background.video.recorder.ads_integration.app_lovin

import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.dt.auto.background.video.recorder.helpers.utils.visible

object AppLovinMrec {
     fun loadMrecAdAppLovin(appLovinAdView: MaxAdView) {
        try {
            appLovinAdView.visible()
            appLovinAdView.setListener(object : MaxAdViewAdListener {
                override fun onAdExpanded(ad: MaxAd) {}
                override fun onAdCollapsed(ad: MaxAd) {}
                override fun onAdLoaded(ad: MaxAd) {
                    Log.d("appLovin", "Native Loaded")
                }

                override fun onAdDisplayed(ad: MaxAd) {}
                override fun onAdHidden(ad: MaxAd) {}
                override fun onAdClicked(ad: MaxAd) {}
                override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                    Log.d("appLovin", "Native failed$error")
                    appLovinAdView.loadAd()
                }

                override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
            })
            appLovinAdView.loadAd()
            appLovinAdView.stopAutoRefresh()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}