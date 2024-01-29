package com.dt.auto.background.video.recorder.ads_integration.facebook

import android.app.Activity
import android.util.Log
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import com.dt.auto.background.video.recorder.AdCheckVariables
import com.dt.auto.background.video.recorder.ads_integration.app_lovin.ApplovinInterstitials

object FacebookInterstitials {
    var fb_inter: InterstitialAd? = null

    fun loadFacebookInterstitial(appCompatActivity: Activity) {
        try {

            fb_inter = InterstitialAd(appCompatActivity, "1387512678377513_1387514138377367")
            val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
                override fun onInterstitialDisplayed(ad: Ad) {}
                override fun onInterstitialDismissed(ad: Ad) {
                    ApplovinInterstitials.loadApplovinInterstitial(appCompatActivity)

                }
                override fun onError(ad: Ad?, adError: AdError) {
                    Log.d("FacebookAds", adError.getErrorMessage())

                    AdCheckVariables.LOAD_FACEBOOK_INTER=false
                    ApplovinInterstitials.loadApplovinInterstitial(appCompatActivity)


                    fb_inter = null
                }

                override fun onAdLoaded(ad: Ad) {
                    Log.d("FacebookAds", "loaded")
                    AdCheckVariables.LOAD_FACEBOOK_INTER=true
                    fb_inter = ad as InterstitialAd

                }

                override fun onAdClicked(ad: Ad) {}
                override fun onLoggingImpression(ad: Ad) {}
            }
            fb_inter!!.loadAd(
                fb_inter?.buildLoadAdConfig()
                    ?.withAdListener(interstitialAdListener)
                    ?.build()
            )


            AdCheckVariables.LOAD_APPLOVIN_INTER = fb_inter?.isAdLoaded != true

        } catch (e: Exception) {

        }


    }

    fun showAd() {
        if (fb_inter==null){
            fb_inter?.loadAd()
            Log.d("FacebookError", "Facebook If: ")

        }else {
            Log.d("FacebookError", "Facebook Else: ")

            if (AdCheckVariables.LOAD_FACEBOOK_INTER) { //check if admob request failed then show Facebook Inter_ad
                try {
                    if (fb_inter?.isAdLoaded == true) {
                        fb_inter?.show()
                    }
                } catch (e: Exception) {
                }
            }
        }
    }
}