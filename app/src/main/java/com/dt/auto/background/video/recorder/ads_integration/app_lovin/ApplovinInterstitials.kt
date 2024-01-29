package com.dt.auto.background.video.recorder.ads_integration.app_lovin

import android.app.Activity
import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.dt.auto.background.video.recorder.AdCheckVariables
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.ads_integration.admob.AdmobInterstitial

object ApplovinInterstitials {


    var interstitialAd: MaxInterstitialAd? = null

    fun loadApplovinInterstitial(appCompatActivity: Activity) {
        try {
            interstitialAd = MaxInterstitialAd(appCompatActivity.getString(R.string.app_lovin_inters_id), appCompatActivity)

            interstitialAd?.setListener(object : MaxAdListener {
                override fun onAdLoaded(ad: MaxAd?) {
                    AdCheckVariables.LOAD_APPLOVIN_INTER=true

                    Log.d("AdmobError", "Applovin onAdLoaded: ")
                }

                override fun onAdDisplayed(ad: MaxAd?) {}

                override fun onAdHidden(ad: MaxAd?) {
                    // Interstitial Ad is hidden. Pre-load the next ad
                    /*
                    * Should start activity from here
                    *
                    * activity will be started on the basis of constants like if constant is moveToExt is true
                    * then after hiding ad we'll move to exit activity from here
                    * */
                    AdCheckVariables.LOAD_APPLOVIN_INTER=false

                    AdmobInterstitial.loadAdInterstitial(appCompatActivity)
                }

                override fun onAdClicked(ad: MaxAd?) {}

                override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                    AdCheckVariables.LOAD_APPLOVIN_INTER=false
                    AdmobInterstitial.loadAdInterstitial(appCompatActivity)

                    // Interstitial ad failed to load. We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds).

                }

                override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                    // Interstitial ad failed to display. We recommend loading the next ad.
                    Log.d("AdmobError", "Applovin onAdDisplayFailed: ")
                    AdCheckVariables.LOAD_APPLOVIN_INTER=false
                    AdmobInterstitial.loadAdInterstitial(appCompatActivity)

                }

            })
            interstitialAd?.setRevenueListener { }

            // Load the first ad.
            interstitialAd?.loadAd()
        } catch (e: Exception) {

        }


    }

    fun showAd() {
        if (interstitialAd==null){
            interstitialAd?.loadAd()
            Log.d("AdmobError", "Applovin If: ")

        }else {
            Log.d("AdmobError", "Applovin Else: ")

            if (AdCheckVariables.LOAD_APPLOVIN_INTER) { //check if admob or facebook request failed then show applovin Inter_ad
                try {
                    if (interstitialAd?.isReady == true) {
                        interstitialAd?.showAd()
                    }
                } catch (e: Exception) {
                }
            }
        }
    }
}