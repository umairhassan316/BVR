package com.dt.auto.background.video.recorder.ads_integration.admob

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dt.auto.background.video.recorder.helpers.utils.log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.dt.auto.background.video.recorder.AdCheckVariables
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.ads_integration.app_lovin.ApplovinInterstitials


object AdmobInterstitial {

    @JvmField
    var mAdIsLoading: Boolean = false

    @JvmField
    var mInterstitialAd: InterstitialAd? = null

    @JvmStatic
    fun loadAdInterstitialAdmobSplash(context: Context) { //call only in splash screen when admob failed call applovin ads
        val adRequest = AdRequest.Builder().build()
        try {
            InterstitialAd.load(
                context, context.getString(R.string.admob_interstitial_ad), adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d("AdmobError", "onAdFailedToLoad: ")
                        mInterstitialAd = null
                        mAdIsLoading = false
                        AdCheckVariables.LOAD_APPLOVIN_INTER=true//set adsResponses true to show another ads request from aplovin
                        ApplovinInterstitials.showAd()
                    }
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d("AdmobError", "onAdLoaded: ")
                        mInterstitialAd = interstitialAd
                        mAdIsLoading = true
                        AdCheckVariables.LOAD_APPLOVIN_INTER=false
                    }
                }
            )
        } catch (e: Exception) {

        }

    }

    @JvmStatic
    fun loadAdInterstitial(context: Activity) {
        val adRequest = AdRequest.Builder().build()
        try {
            InterstitialAd.load(
                context, context.getString(R.string.admob_interstitial_ad), adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d("AdmobError", "onAdFailedToLoad: ")
                        mInterstitialAd = null
                        mAdIsLoading = false
                        AdCheckVariables.LOAD_ADMOB_INTER=false
                       // FacebookInterstitials.loadFacebookInterstitial(context)
                        AdCheckVariables.LOAD_APPLOVIN_INTER=true//set adsResponses true to show another ads request from aplovin
                        ApplovinInterstitials.showAd()
                    }
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d("AdmobError", "onAdLoaded: ")
                        mInterstitialAd = interstitialAd
                        mAdIsLoading = true
                        AdCheckVariables.LOAD_ADMOB_INTER=true
                    }
                }
            )
        } catch (e: Exception) {

        }

    }

    @JvmStatic
    fun showInterstitialAd(activity: AppCompatActivity) {

        if (mInterstitialAd != null && mAdIsLoading) {

            mInterstitialAd?.show(activity)
            mInterstitialAd?.fullScreenContentCallback =

                object : FullScreenContentCallback(){
                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        log("Ad was dismissed.")
                        mInterstitialAd = null
                        mAdIsLoading = false
                        loadAdInterstitial(activity)
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        log("Ad failed to show ${p0.message}")
                        mInterstitialAd = null
                        mAdIsLoading = false
                    }

                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                        log("Ad showed fullscreen content.")

                    }
                }

        } else {
            loadAdInterstitial(activity)
        }


    }
}